package com.example.E_Library.service;

import com.example.E_Library.model.Book;
import com.example.E_Library.model.BookCategory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
public class SupabaseService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // public SupabaseService() {
    // this.restTemplate = new RestTemplate();
    // this.restTemplate.setRequestFactory(new
    // HttpComponentsClientHttpRequestFactory());
    // }

    @Value("${PROJECT_URL}")
    private String projectUrl;

    @Value("${SECRET_KEY}")
    private String secretKey;

    @Value("${app.download.path:${java.io.tmpdir}/downloads}")
    private String downloadPath;
    
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", secretKey);
        headers.set("Authorization", "Bearer " + secretKey);
        return headers;
    }

    // ===== BOOK CRUD =====
    public String getAllBooks() {
        String url = projectUrl + "/rest/v1/books";
        HttpEntity<String> entity = new HttpEntity<>(getHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    public String getBookById(String id) {
        String url = projectUrl + "/rest/v1/books?book_id=eq." + id;
        HttpEntity<String> entity = new HttpEntity<>(getHeaders());
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "Error fetching book: " + e.getResponseBodyAsString();
        }
    }

    public String addBook(Book book) {
        String url = projectUrl + "/rest/v1/books";

        Map<String, Object> payload = new HashMap<>();
        if (book.getTitle() != null)
            payload.put("title", book.getTitle());
        if (book.getAuthor() != null)
            payload.put("author", book.getAuthor());
        if (book.getDescription() != null)
            payload.put("description", book.getDescription());
        if (book.getFile_url() != null)
            payload.put("file_url", book.getFile_url());
        if (book.getUploaded_by() != null)
            payload.put("uploaded_by", book.getUploaded_by());
        if (book.getUploaded_at() != null)
            payload.put("uploaded_at", book.getUploaded_at());
        if (book.getCategory_id() != null)
            payload.put("category_id", book.getCategory_id());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, getHeaders());

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "Error adding book: " + e.getResponseBodyAsString();
        }
    }

    // public String updateBook(String id, Map<String, Object> updates) {
    // String url = projectUrl + "/rest/v1/books?book_id=eq." + id;
    // HttpEntity<Map<String, Object>> entity = new HttpEntity<>(updates,
    // getHeaders());
    // try {
    // ResponseEntity<String> response = restTemplate.exchange(url,
    // HttpMethod.PATCH, entity, String.class);
    // return response.getBody();
    // } catch (Exception e) {
    // e.printStackTrace(); // See full error in console
    // return "Error updating book: " + e.getMessage();
    // }
    // }

    public String updateBook(String id, Map<String, Object> updates) {
        // Supabase needs the filter on book_id in the URL
        String url = projectUrl + "/rest/v1/books?book_id=eq." + id;

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(updates, getHeaders());
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error updating book: " + e.getMessage();
        }
    }

    public String deleteBook(String id) {
        String url = projectUrl + "/rest/v1/books?book_id=eq." + id;
        HttpEntity<String> entity = new HttpEntity<>(getHeaders());
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "Error deleting book: " + e.getResponseBodyAsString();
        }
    }

    // ===== CATEGORY CRUD =====
    public String getAllCategories() {
        String url = projectUrl + "/rest/v1/book_categories";
        HttpEntity<String> entity = new HttpEntity<>(getHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    public String addCategory(BookCategory category) {
        String url = projectUrl + "/rest/v1/book_categories";

        Map<String, Object> payload = new HashMap<>();
        if (category.getCategory_id() != null)
            payload.put("category_id", category.getCategory_id());
        if (category.getCategory_name() != null)
            payload.put("category_name", category.getCategory_name());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, getHeaders());

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "Error adding category: " + e.getResponseBodyAsString();
        }
    }

    // public String updateCategory(String id, Map<String, Object> updates) {
    // String url = projectUrl + "/rest/v1/book_categories?category_id=eq." + id;
    // HttpEntity<Map<String, Object>> entity = new HttpEntity<>(updates,
    // getHeaders());
    // try {
    // ResponseEntity<String> response = restTemplate.exchange(url,
    // HttpMethod.PATCH, entity, String.class);
    // return response.getBody();
    // } catch (HttpClientErrorException e) {
    // return "Error updating category: " + e.getResponseBodyAsString();
    // }
    // }

    public String updateCategory(String id, Map<String, Object> updates) {
        String url = projectUrl + "/rest/v1/book_categories?category_id=eq." + id;

        // Prevent accidental overwrite of category_id
        updates.remove("category_id");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(updates, getHeaders());
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "Error updating category: " + e.getResponseBodyAsString();
        }
    }

    public String deleteCategory(String id) {
        String url = projectUrl + "/rest/v1/book_categories?category_id=eq." + id;
        HttpEntity<String> entity = new HttpEntity<>(getHeaders());
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "Error deleting category: " + e.getResponseBodyAsString();
        }
    }

    // search
    public String searchBooks(String query) {
        // Search in title or author
        String url = projectUrl + "/rest/v1/books?or=(title.ilike.*" + query + "*,author.ilike.*" + query + "*)";

        HttpEntity<String> entity = new HttpEntity<>(getHeaders());
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "Error searching books: " + e.getResponseBodyAsString();
        }
    }

    // ===== DOWNLOAD MULTIPLE BOOKS =====
    public String downloadMultiple(List<String> bookIds) {
        try {
            Path downloadDir = Paths.get(downloadPath);
            if (!Files.exists(downloadDir)) {
                Files.createDirectories(downloadDir);
            }
            String zipFileName = "books_" + UUID.randomUUID().toString() + ".zip";
            Path zipFilePath = downloadDir.resolve(zipFileName);
            try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
                for (String bookId : bookIds) {
                    try {
                        String bookResponse = getBookById(bookId);
                        JsonNode bookArray = objectMapper.readTree(bookResponse);
                        if (bookArray.isArray() && bookArray.size() > 0) {
                            JsonNode book = bookArray.get(0);
                            String fileUrl = book.get("file_url").asText();
                            String title = book.get("title").asText();
                            String author = book.get("author").asText();
                            String fileName = sanitizeFileName(title + "_" + author + ".pdf");
                            downloadAndAddToZip(fileUrl, fileName, zipOut);
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing book ID " + bookId + ": " + e.getMessage());
                    }
                }
            }
            Map<String, String> response = new HashMap<>();
            response.put("zipFilePath", zipFilePath.toString());
            response.put("downloadUrl", "/downloads/" + zipFileName);
            response.put("message", "Successfully created zip file with " + bookIds.size() + " books");
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error creating zip file: " + e.getMessage();
        }
    }

    private void downloadAndAddToZip(String fileUrl, String fileName, ZipOutputStream zipOut) throws IOException {
        try {
            URL url = new URL(fileUrl);
            try (InputStream inputStream = url.openStream();
                 BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
                ZipEntry zipEntry = new ZipEntry(fileName);
                zipOut.putNextEntry(zipEntry);                
                byte[] buffer = new byte[1024];
                int length;
                while ((length = bufferedInputStream.read(buffer)) >= 0) {
                    zipOut.write(buffer, 0, length);
                }                
                zipOut.closeEntry();
            }
        } catch (Exception e) {
            System.err.println("Error downloading file from URL " + fileUrl + ": " + e.getMessage());
            throw new IOException("Failed to download file: " + fileUrl, e);
        }
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_").substring(0, Math.min(fileName.length(), 100));
    }

}
