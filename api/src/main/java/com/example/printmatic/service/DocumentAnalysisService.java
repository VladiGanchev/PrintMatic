package com.example.printmatic.service;

import com.example.printmatic.dto.response.FileAnalysisResultDTO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class DocumentAnalysisService {

    public FileAnalysisResultDTO analyzeDocument(MultipartFile file, boolean grayscale) throws IOException {
        String originalFileName = file.getOriginalFilename();


        if (isPdf(originalFileName)) {
            return analyzePdf(file.getBytes(), grayscale);
        } else if (isImage(originalFileName)) {
            return analyzeImage(file);
        } else {
            throw new IOException("Unsupported file type");
        }
    }

    private FileAnalysisResultDTO analyzeImage(MultipartFile file) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        boolean isColor = isColorPage(bufferedImage);

        return new FileAnalysisResultDTO(
            1,
                isColor? 1 : 0,
                isColor? 0 : 1
        );
    }


    private FileAnalysisResultDTO analyzePdf(byte[] pdfBytes, boolean grayscale) throws IOException {
        int colorPages = 0;
        int totalPages;

        try(PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFRenderer renderer = new PDFRenderer(document);
            totalPages = document.getNumberOfPages();
            if(grayscale){
                return new FileAnalysisResultDTO(
                        totalPages,
                        0,
                        totalPages
                );
            }

            for(int currentPage = 0; currentPage < totalPages; currentPage++) {
                BufferedImage image = renderer.renderImageWithDPI(currentPage, 72);
                if(isColorPage(image))
                    colorPages++;
            }
        }

        return new FileAnalysisResultDTO(
                totalPages,
                colorPages,
                totalPages-colorPages
        );
    }

    private boolean isColorPage(BufferedImage image) {
        int sampleSize = 10;
        int colorThreshold = 10;

        for (int x = 0; x < image.getWidth(); x += sampleSize) {
            for (int y = 0; y < image.getHeight(); y += sampleSize) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                if (Math.abs(red - green) > colorThreshold ||
                        Math.abs(red - blue) > colorThreshold ||
                        Math.abs(green - blue) > colorThreshold) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isPdf(String fileName) {
        if(fileName == null || fileName.isEmpty())
            return false;
        return fileName.toLowerCase().endsWith(".pdf");
    }

    private boolean isImage(String fileName) {
        if(fileName == null || fileName.isEmpty()) return false;
        return fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".png");
    }

    private boolean isWordDocument(String fileName) {
        if(fileName == null || fileName.isEmpty()) return false;
        return fileName.toLowerCase().endsWith(".docx")
                || fileName.toLowerCase().endsWith(".doc");
    }
}
