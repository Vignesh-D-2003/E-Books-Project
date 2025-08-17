package com.example.E_Library;

import com.example.E_Library.controller.FileController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final Path uploadDir = Paths.get("uploads");
    private final Path testFile = uploadDir.resolve("test.pdf");

    @BeforeEach
    void setUp() throws Exception {
        // Ensure uploads directory exists
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Create a dummy PDF file for testing
        if (!Files.exists(testFile)) {
            Files.write(testFile, "dummy pdf content".getBytes());
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN") // 👈 matches @PreAuthorize("hasRole('ADMIN')")
    void testServeFileSuccess() throws Exception {
        mockMvc.perform(get("/uploads/test.pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition", "inline; filename=\"test.pdf\""));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testServeFileNotFound() throws Exception {
        mockMvc.perform(get("/uploads/notfound.pdf"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        // no @WithMockUser here → should fail security
        mockMvc.perform(get("/uploads/test.pdf"))
                .andExpect(status().isUnauthorized()); // or Forbidden if auth required
    }
}
