package com.example.printmatic.service;

import com.example.printmatic.dto.response.FileAnalysisResultDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DocumentAnalysisServiceTest {

    @InjectMocks
    private DocumentAnalysisService documentAnalysisService;

    private MockMultipartFile colorPdfFile;
    private MockMultipartFile grayscalePdfFile;
    private MockMultipartFile colorImageFile;
    private MockMultipartFile grayscaleImageFile;
    private MockMultipartFile unsupportedFile;

    @BeforeEach
    void setUp() throws IOException {

        byte[] colorPdfBytes = createTestPdf(true);
        colorPdfFile = new MockMultipartFile(
                "test.pdf",
                "test.pdf",
                "application/pdf",
                colorPdfBytes
        );

        byte[] grayscalePdfBytes = createTestPdf(false);
        grayscalePdfFile = new MockMultipartFile(
                "test.pdf",
                "test.pdf",
                "application/pdf",
                grayscalePdfBytes
        );

        byte[] colorImageBytes = createTestImage(true);
        colorImageFile = new MockMultipartFile(
                "test.jpg",
                "test.jpg",
                "image/jpeg",
                colorImageBytes
        );

        byte[] grayscaleImageBytes = createTestImage(false);
        grayscaleImageFile = new MockMultipartFile(
                "test.jpg",
                "test.jpg",
                "image/jpeg",
                grayscaleImageBytes
        );

        unsupportedFile = new MockMultipartFile(
                "test.txt",
                "test.txt",
                "text/plain",
                "test content".getBytes()
        );
    }

    @Test
    void analyzeDocument_ColorPDF_Success() throws IOException {
        FileAnalysisResultDTO result = documentAnalysisService.analyzeDocument(colorPdfFile, false);

        assertEquals(1, result.totalPages());
        assertEquals(1, result.colorfulPages());
        assertEquals(0, result.grayscalePages());
    }

    @Test
    void analyzeDocument_GrayscalePDF_Success() throws IOException {
        FileAnalysisResultDTO result = documentAnalysisService.analyzeDocument(grayscalePdfFile, false);

        assertEquals(1, result.totalPages());
        assertEquals(0, result.colorfulPages());
        assertEquals(1, result.grayscalePages());
    }

    @Test
    void analyzeDocument_ForcedGrayscalePDF_Success() throws IOException {
        FileAnalysisResultDTO result = documentAnalysisService.analyzeDocument(colorPdfFile, true);

        assertEquals(1, result.totalPages());
        assertEquals(0, result.colorfulPages());
        assertEquals(1, result.grayscalePages());
    }

    @Test
    void analyzeDocument_ColorImage_Success() throws IOException {
        FileAnalysisResultDTO result = documentAnalysisService.analyzeDocument(colorImageFile, false);

        assertEquals(1, result.totalPages());
        assertEquals(1, result.colorfulPages());
        assertEquals(0, result.grayscalePages());
    }

    @Test
    void analyzeDocument_GrayscaleImage_Success() throws IOException {
        FileAnalysisResultDTO result = documentAnalysisService.analyzeDocument(grayscaleImageFile, false);

        assertEquals(1, result.totalPages());
        assertEquals(0, result.colorfulPages());
        assertEquals(1, result.grayscalePages());
    }

    @Test
    void analyzeDocument_UnsupportedFile_ThrowsException() {
        assertThrows(IOException.class, () ->
                documentAnalysisService.analyzeDocument(unsupportedFile, false)
        );
    }

    @Test
    void isPdf_ValidPDF_ReturnsTrue() {
        assertTrue(documentAnalysisService.isPdf("test.pdf"));
        assertTrue(documentAnalysisService.isPdf("test.PDF"));
    }

    @Test
    void isPdf_InvalidPDF_ReturnsFalse() {
        assertFalse(documentAnalysisService.isPdf("test.jpg"));
        assertFalse(documentAnalysisService.isPdf(""));
        assertFalse(documentAnalysisService.isPdf(null));
    }

    private byte[] createTestPdf(boolean isColor) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                if (isColor) {
                    contentStream.setStrokingColor(Color.RED);
                    contentStream.setNonStrokingColor(Color.BLUE);
                } else {
                    contentStream.setStrokingColor(Color.BLACK);
                    contentStream.setNonStrokingColor(Color.GRAY);
                }

                contentStream.addRect(100, 100, 100, 100);
                contentStream.fill();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    private byte[] createTestImage(boolean isColor) throws IOException {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        if (isColor) {
            g2d.setColor(Color.RED);
            g2d.fillRect(0, 0, 50, 100);
            g2d.setColor(Color.BLUE);
            g2d.fillRect(50, 0, 50, 100);
        } else {
            g2d.setColor(Color.GRAY);
            g2d.fillRect(0, 0, 100, 100);
        }
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }
}