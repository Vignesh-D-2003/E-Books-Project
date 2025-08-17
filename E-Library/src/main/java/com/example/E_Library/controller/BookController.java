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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/books")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private SupabaseService supabaseService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String getAllBooks() throws Exception {
        String jsonResponse = supabaseService.getAllBooks();

        ArrayNode books = (ArrayNode) objectMapper.readTree(jsonResponse);

        for (JsonNode book : books) {
            if (book.has("file_url")) {
                String fileUrl = book.get("file_url").asText();
                ((ObjectNode) book).put("file_url", "http://localhost:8080" + fileUrl);
            }
        }
        logger.debug("Successfully processed {} books.", books.size());
        return books.toString();
    }

    @GetMapping("/{book_id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String getBookById(@PathVariable Integer book_id) throws Exception {
        logger.info("Request received to get book by ID: {}", book_id);
        String jsonResponse = supabaseService.getBookById(book_id.toString());

        ArrayNode books = (ArrayNode) objectMapper.readTree(jsonResponse);

        if (books.size() > 0) {
            ObjectNode book = (ObjectNode) books.get(0);
            if (book.has("file_url")) {
                String fileUrl = book.get("file_url").asText();
                book.put("file_url", "http://localhost:8080" + fileUrl);
            }
            logger.debug("Book with ID {} found.", book_id);
            return book.toString();
        }
        logger.warn("No book found for ID: {}", book_id);
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
            logger.info("Request received to add a new book.");

        // Convert JSON string to Book object
        ObjectMapper mapper = new ObjectMapper();
        Book book;
        try {
            book = mapper.readValue(bookJson, Book.class);
            logger.debug("Book JSON parsed successfully for title: {}", book.getTitle());
        } catch (IOException e) {
            logger.error("Invalid book JSON received during add book request.", e);
            throw new RuntimeException("Invalid book JSON: " + e.getMessage());
        }

        // Save file and set file_url
        String filePath = savePdfToUploads(pdfFile);
        book.setFile_url(filePath);
        logger.info("PDF file saved to path: {}", filePath);

        return supabaseService.addBook(book);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateBook(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        logger.info("Request received to update book with ID: {}", id);
        // Supabase needs book_id in the body for PUT
        updates.put("book_id", Long.parseLong(id));
        return supabaseService.updateBook(id, updates);
    }

    @DeleteMapping("/{book_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteBook(@PathVariable Integer book_id) {
        logger.info("Request received to delete book with ID: {}", book_id);
        return supabaseService.deleteBook(book_id.toString());
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String searchBooks(@RequestParam("query") String query) {

        logger.info("Request received to search books with query: '{}'", query);
        return supabaseService.searchBooks(query);
    }

    @PostMapping("/download-multiple")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String downloadMultiple(@RequestBody List<String> bookIds) {
        logger.info("Request received to download multiple books. Count: {}", bookIds.size());
        return supabaseService.downloadMultiple(bookIds);
    }
}
