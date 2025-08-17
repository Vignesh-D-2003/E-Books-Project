package com.example.E_Library.controller;

import com.example.E_Library.model.Book;
import com.example.E_Library.service.SupabaseService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class BookController {

    @Autowired
    private SupabaseService supabaseService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    // Temporarily permit all for debugging fetch issue (was
    // @PreAuthorize("hasAnyRole('USER','ADMIN')"))
    public String getAllBooks() throws Exception {
        String jsonResponse = supabaseService.getAllBooks();

        ArrayNode books = (ArrayNode) objectMapper.readTree(jsonResponse);

        for (JsonNode book : books) {
            if (book.has("file_url")) {
                String fileUrl = book.get("file_url").asText();
                // Only prepend localhost if the URL is not already complete
                if (!fileUrl.startsWith("http://") && !fileUrl.startsWith("https://")) {
                    ((ObjectNode) book).put("file_url", "http://localhost:8080" + fileUrl);
                }
            }
        }
        return books.toString();
    }

    @GetMapping("/{book_id}")
    // Temporarily permit all for debugging (was
    // @PreAuthorize("hasAnyRole('USER','ADMIN')"))
    public String getBookById(@PathVariable Integer book_id) throws Exception {
        String jsonResponse = supabaseService.getBookById(book_id.toString());

        ArrayNode books = (ArrayNode) objectMapper.readTree(jsonResponse);

        if (books.size() > 0) {
            ObjectNode book = (ObjectNode) books.get(0);
            if (book.has("file_url")) {
                String fileUrl = book.get("file_url").asText();
                // Only prepend localhost if the URL is not already complete
                if (!fileUrl.startsWith("http://") && !fileUrl.startsWith("https://")) {
                    book.put("file_url", "http://localhost:8080" + fileUrl);
                }
            }
            return book.toString();
        }
        return "{}";
    }

    private String savePdfToUploads(MultipartFile file) {
        try {
            // Get absolute path to project root
            String projectRoot = new File(".").getCanonicalPath();
            String uploadsDir = projectRoot + File.separator + "uploads";

            File dir = new File(uploadsDir);
            if (!dir.exists())
                dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File dest = new File(dir, fileName);
            file.transferTo(dest);

            // Return relative path for Supabase
            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public String addBook(
            @RequestPart("book") String bookJson,
            @RequestPart("pdf") MultipartFile pdfFile) {

        // Convert JSON string to Book object
        ObjectMapper mapper = new ObjectMapper();
        Book book;
        try {
            book = mapper.readValue(bookJson, Book.class);
        } catch (IOException e) {
            throw new RuntimeException("Invalid book JSON: " + e.getMessage());
        }

        // Save file and set file_url
        String filePath = savePdfToUploads(pdfFile);
        book.setFile_url(filePath);

        return supabaseService.addBook(book);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateBook(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        // Supabase needs book_id in the body for PUT
        updates.put("book_id", Long.parseLong(id));
        return supabaseService.updateBook(id, updates);
    }

    @DeleteMapping("/{book_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteBook(@PathVariable Integer book_id) {
        return supabaseService.deleteBook(book_id.toString());
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String searchBooks(@RequestParam("query") String query) {
        return supabaseService.searchBooks(query);
    }

    @PostMapping("/download-multiple")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String downloadMultiple(@RequestBody List<String> bookIds) {
        return supabaseService.downloadMultiple(bookIds);
    }
}
