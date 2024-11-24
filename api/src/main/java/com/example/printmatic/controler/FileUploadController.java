package com.example.printmatic.controler;

import com.example.printmatic.dto.response.FileUploadResponseDTO;
import com.example.printmatic.dto.response.MessageResponseDTO;
import com.example.printmatic.dto.response.UploadResultDTO;
import com.example.printmatic.service.GoogleCloudStorageService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.example.printmatic.enums.RoleEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/storage")
public class FileUploadController {
    private final GoogleCloudStorageService gcsService;

    public FileUploadController(GoogleCloudStorageService gcsService) {
        this.gcsService = gcsService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponseDTO> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            UploadResultDTO result = gcsService.uploadFile(file, email);
            return ResponseEntity.ok(new FileUploadResponseDTO(
                    200,
                    "File uploaded successfully",
                    result.blobName(),
                    result.fileAnalysisResult().totalPages(),
                    result.fileAnalysisResult().colorfulPages(),
                    result.fileAnalysisResult().grayscalePages()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new FileUploadResponseDTO(
                            400,
                            "Could not upload file: " + e.getMessage(),
                            null,
                            0,
                            0,
                            0
                    ));
        }
    }

    @GetMapping("/download")
    public ResponseEntity<MessageResponseDTO> getDownloadUrl(@RequestParam("blobName") String blobName) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            boolean isEmployeeOrAdmin = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .peek(role -> System.out.println("Checking role: " + role))
                    .anyMatch(role -> role.equals("ADMIN") || role.equals("EMPLOYEE"));

            if (!isEmployeeOrAdmin) {
                return ResponseEntity.status(403)
                        .body(new MessageResponseDTO(403, "Access denied: Employee or Admin role required"));
            }

            String downloadUrl = gcsService.generateDownloadUrl(blobName);
            return ResponseEntity.ok(new MessageResponseDTO(200, downloadUrl));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponseDTO(400, "Could not generate download URL: " + e.getMessage()));
        }
    }
}
