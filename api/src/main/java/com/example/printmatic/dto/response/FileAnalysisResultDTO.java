package com.example.printmatic.dto.response;

public record FileAnalysisResultDTO(
        int totalPages,
        int colorfulPages,
        int grayscalePages
) {
}