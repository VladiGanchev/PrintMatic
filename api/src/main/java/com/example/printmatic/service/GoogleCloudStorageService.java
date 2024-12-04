package com.example.printmatic.service;

import com.example.printmatic.dto.response.FileAnalysisResultDTO;
import com.example.printmatic.dto.response.UploadResultDTO;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class GoogleCloudStorageService {
    private final Storage clientStorage;
    private final Storage employeeStorage;
    private final String bucketName;
    private final DocumentAnalysisService documentAnalysisService;

    public GoogleCloudStorageService(
            @Value("${gcp.bucket.name}") String bucketName,
            @Value("${gcp.client.service-account}") String clientServiceAccountPath,
            @Value("${gcp.employee.service-account}") String employeeServiceAccountPath,
            DocumentAnalysisService documentAnalysisService
    ) throws IOException {
        this.bucketName = bucketName;
        this.documentAnalysisService = documentAnalysisService;

        // Initialize client storage (for uploads)
        ServiceAccountCredentials clientCredentials = ServiceAccountCredentials
                .fromStream(new FileInputStream(clientServiceAccountPath));
        this.clientStorage = StorageOptions.newBuilder()
                .setCredentials(clientCredentials)
                .build()
                .getService();

        // Initialize employee storage (for downloads)
        ServiceAccountCredentials employeeCredentials = ServiceAccountCredentials
                .fromStream(new FileInputStream(employeeServiceAccountPath));
        this.employeeStorage = StorageOptions.newBuilder()
                .setCredentials(employeeCredentials)
                .build()
                .getService();
    }

    public UploadResultDTO uploadFile(MultipartFile file, String userEmail, boolean grayscale) throws IOException {

        FileAnalysisResultDTO analysisResultDTO = documentAnalysisService.analyzeDocument(file,grayscale);

        String blobName = String.format(
                "orders/%s/%d-%s",
                userEmail,
                System.currentTimeMillis(),
                file.getOriginalFilename()
        );

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blobName)
                .setContentType(file.getContentType())
                .build();

        clientStorage.create(blobInfo, file.getBytes());
        return new UploadResultDTO(
                blobName,
                analysisResultDTO
        );
    }

    public String generateDownloadUrl(String blobName) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blobName).build();

        return employeeStorage.signUrl(
                blobInfo,
                5,
                TimeUnit.DAYS,
                Storage.SignUrlOption.httpMethod(HttpMethod.GET)
        ).toString();
    }
}

