package com.example.printmatic.dto.response;

public record UploadResultDTO(
        String blobName,
        FileAnalysisResultDTO fileAnalysisResult
) {
}
