package com.example.E_Library.service;

import com.example.E_Library.model.Book;
import com.example.E_Library.model.BookCategory;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SupabaseService {
    private final RestTemplate restTemplate = new RestTemplate();

    // public SupabaseService() {
    // this.restTemplate = new RestTemplate();
    // this.restTemplate.setRequestFactory(new
    // HttpComponentsClientHttpRequestFactory());
    // }

    @Value("${PROJECT_URL}")
    private String projectUrl;

    @Value("${SECRET_KEY}")
    private String secretKey;

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
        String url = projectUrl + "/rest/v1/books?on_conflict=book_id"; // upsert
        updates.put("book_id", id); // ensure ID is included in payload

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(updates, getHeaders());
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
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
        String url = projectUrl + "/rest/v1/book_categories?on_conflict=category_id"; // upsert
        updates.put("category_id", id); // include ID

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(updates, getHeaders());
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error updating category: " + e.getMessage();
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
}
