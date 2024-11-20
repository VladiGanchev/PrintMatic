package com.example.printmatic.controler;

import com.example.printmatic.dto.response.MessageResponseDTO;
import com.example.printmatic.service.GoogleCloudStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class FileUploadController {
    private final GoogleCloudStorageService gcsService;

    public FileUploadController(GoogleCloudStorageService gcsService) {
        this.gcsService = gcsService;
    }

    @PostMapping
    private ResponseEntity<MessageResponseDTO> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileURL = gcsService.uploadFile(file);
            return ResponseEntity.ok(new MessageResponseDTO(200, fileURL));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO(400, "could not upload file"));
        }

    }
}
