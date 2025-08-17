package com.example.E_Library.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/uploads")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    private final Path uploadDir = Paths.get("uploads");

    @GetMapping("/{filename:.+}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws MalformedURLException {
        logger.info("Request received to serve file: {}", filename);
        Path file = uploadDir.resolve(filename).normalize();
        Resource resource = new UrlResource(file.toUri());

        if (resource.exists()) {
            logger.debug("File '{}' found. Preparing to stream as PDF.", filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF) // 👈 PDF content type
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            logger.warn("File requested but not found: {}", filename);
            return ResponseEntity.notFound().build();
        }
    }
}
