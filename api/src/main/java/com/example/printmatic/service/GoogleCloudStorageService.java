package com.example.printmatic.service;


import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class GoogleCloudStorageService {
   private Storage storage;
   private final String bucketName;

    public GoogleCloudStorageService(@Value("${gcp.bucket.name}") String bucketName) {
        this.bucketName = bucketName;
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, file.getOriginalFilename()).build();
        storage.create(blobInfo, file.getBytes());
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, file.getOriginalFilename());
   }
}
