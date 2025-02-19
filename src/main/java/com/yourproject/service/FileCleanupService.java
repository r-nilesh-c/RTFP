package com.yourproject.service;

import com.yourproject.utils.CodeGenerator;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.logging.Logger;

import static java.nio.file.Files.readString;

@Service
public class FileCleanupService implements DisposableBean {
    private static final Logger logger = Logger.getLogger(FileCleanupService.class.getName());
    private static final long EXPIRATION_TIME = 600000; // 10 minutes

    @Value("${file.upload.dir}")
    private String uploadDir;

    public void cleanupExpiredFiles() {
        try {
            Files.list(Paths.get(uploadDir)).forEach(path -> {
                try {
                    if (Files.exists(path.resolve("metadata.txt"))) {
                        String[] metadata = readString(path.resolve("metadata.txt")).split("\n");
                        long timestamp = Long.parseLong(metadata[2]);
                        if (System.currentTimeMillis() - timestamp > EXPIRATION_TIME) {
                            deleteDirectory(path);
                            CodeGenerator.removeCode(path.getFileName().toString());
                            logger.info("Cleaned up expired file: " + path.getFileName());
                        }
                    }
                } catch (Exception e) {
                    logger.warning("Error cleaning up file: " + path + " - " + e.getMessage());
                }
            });
        } catch (Exception e) {
            logger.severe("Error during cleanup: " + e.getMessage());
        }
    }

    public void deleteDirectory(Path path) {
        try {
            Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        logger.warning("Error deleting file: " + p + " - " + e.getMessage());
                    }
                });
        } catch (IOException e) {
            logger.severe("Error deleting directory: " + path + " - " + e.getMessage());
        }
    }

    @PreDestroy
    public void destroy() throws Exception {
        logger.info("Server shutting down - cleaning up all files");
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (Files.exists(uploadPath)) {
                Files.walk(uploadPath)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            logger.warning("Error deleting file during shutdown: " + path);
                        }
                    });
            }
        } catch (Exception e) {
            logger.severe("Error during shutdown cleanup: " + e.getMessage());
        }
    }
}
