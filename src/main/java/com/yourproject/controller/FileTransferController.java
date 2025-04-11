package com.yourproject.controller;

import com.yourproject.utils.CodeGenerator;
import com.yourproject.service.FileCleanupService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;
import java.nio.file.*;
import java.io.*;
import com.yourproject.model.FileData;
import com.yourproject.utils.CustomByteCipher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.UUID;
import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.cors.CorsConfiguration;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FileTransferController {
    private static final Logger logger = LoggerFactory.getLogger(FileTransferController.class);

    @Value("${file.upload.dir}")
    private String uploadDir;
    private final FileCleanupService fileCleanupService;

    @Autowired
    public FileTransferController(FileCleanupService fileCleanupService) {
        this.fileCleanupService = fileCleanupService;
    }

    private static final long EXPIRATION_TIME = 600000; // 10 minutes in milliseconds

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!");
        }
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("No file provided"));
        }

        logger.debug("Received upload request for file: {}", file.getOriginalFilename());
        try {
            byte[] fileBytes = file.getBytes();
            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            SecretKey secretKey = CustomByteCipher.generateKey(); // Generate AES key
            String fileId = CodeGenerator.generateUniqueCode();

            // Create directory for this file
            Path fileDir = Paths.get(uploadDir, fileId);
            Files.createDirectories(fileDir);

            // Save metadata
            Path metadataPath = fileDir.resolve("metadata.txt");
            long timestamp = System.currentTimeMillis();
            Files.write(metadataPath,
                    (fileName + "\n" + contentType + "\n" + timestamp + "\n" + Base64.getEncoder().encodeToString(secretKey.getEncoded())).getBytes());

            // Save encrypted file
            Path filePath = fileDir.resolve("content");
            byte[] encryptedBytes = CustomByteCipher.encrypt(fileBytes, secretKey); // Encrypt using AES
            Files.write(filePath, encryptedBytes);

            // Save encryption keys
            System.setProperty("encryption.keys", fileDir.resolve("keys").toString());

            logger.info("Successfully uploaded file: {} with ID: {}", fileName, fileId);
            return ResponseEntity.ok().body(new SuccessResponse(fileId));
        } catch (Exception e) {
// Update error handling to provide better feedback for JSON responses
            logger.error("Error uploading file: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new ErrorResponse("An error occurred while uploading the file. Please try again.")); // More user-friendly message
        }
    }

    @GetMapping("/download/{fileId}")
    @ResponseBody
    public ResponseEntity<?> downloadFile(@PathVariable String fileId) {
        try {
            Path fileDir = Paths.get(uploadDir, fileId);
            if (!Files.exists(fileDir)) {
                return ResponseEntity.notFound().build();
            }

            // Read metadata
            String[] metadata = Files.readString(fileDir.resolve("metadata.txt")).split("\n");
            long timestamp = Long.parseLong(metadata[2]);
            SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(metadata[3]), "AES"); // Retrieve AES key

            // Check expiration
            if (System.currentTimeMillis() - timestamp > EXPIRATION_TIME) {
                fileCleanupService.deleteDirectory(fileDir);
                CodeGenerator.removeCode(fileId);
                return ResponseEntity.status(410)
                        .body(new ErrorResponse("File has expired")); // Return JSON error response
            }

            // Read and decrypt file
            byte[] encryptedBytes = Files.readAllBytes(fileDir.resolve("content"));
            byte[] decryptedBytes = CustomByteCipher.decrypt(encryptedBytes, secretKey); // Decrypt using AES

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata[0] + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, metadata[1]);

            return ResponseEntity.ok().headers(headers).body(decryptedBytes);
        } catch (Exception e) {
            logger.error("Error downloading file: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("An error occurred while downloading the file. Please try again.")); // More user-friendly message
        }
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    public void cleanupExpiredFiles() {
        fileCleanupService.cleanupExpiredFiles();
    }
}

// Add these classes at the bottom of the file:
class ErrorResponse {
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}

class SuccessResponse {
    private String fileId;

    public SuccessResponse(String fileId) {
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }
}
