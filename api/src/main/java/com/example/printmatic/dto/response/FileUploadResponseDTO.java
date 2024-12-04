package com.example.printmatic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileUploadResponseDTO {
    private int status;
    private String message;
    private String blobName;
    private int totalPages;
    private int colorfulPages;
    private int grayscalePages;
}

