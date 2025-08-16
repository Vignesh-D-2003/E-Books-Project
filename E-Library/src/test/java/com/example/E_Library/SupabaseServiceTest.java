package com.example.E_Library;

import com.example.E_Library.model.Book;
import com.example.E_Library.model.BookCategory;
import com.example.E_Library.service.SupabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SupabaseServiceTest {

    @InjectMocks
    private SupabaseService supabaseService;

    @Mock
    private RestTemplate restTemplate;

    @Value("${PROJECT_URL:http://fake-url}")
    private String projectUrl;

    @Value("${SECRET_KEY:fake-secret}")
    private String secretKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // inject fake config values manually
        supabaseService = new SupabaseService();
        // replace its restTemplate with mocked one
        supabaseService.getClass()
                .getDeclaredFields();
        // hack: use reflection to set values
        try {
            var f1 = SupabaseService.class.getDeclaredField("projectUrl");
            f1.setAccessible(true);
            f1.set(supabaseService, projectUrl);

            var f2 = SupabaseService.class.getDeclaredField("secretKey");
            f2.setAccessible(true);
            f2.set(supabaseService, secretKey);

            var f3 = SupabaseService.class.getDeclaredField("restTemplate");
            f3.setAccessible(true);
            f3.set(supabaseService, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetAllBooks_success() {
        String expected = "[{\"book_id\":1,\"title\":\"Java\"}]";
        when(restTemplate.exchange(
                eq(projectUrl + "/rest/v1/books"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));

        String result = supabaseService.getAllBooks();
        assertEquals(expected, result);
    }

    @Test
    void testGetBookById_success() {
        String expected = "[{\"book_id\":1,\"title\":\"Spring Boot\"}]";
        when(restTemplate.exchange(
                eq(projectUrl + "/rest/v1/books?book_id=eq.1"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));

        String result = supabaseService.getBookById("1");
        assertEquals(expected, result);
    }

    @Test
    void testGetBookById_error() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not found"));

        String result = supabaseService.getBookById("99");
        assertTrue(result.contains("Error fetching book"));
    }

    @Test
    void testAddBook_success() {
        Book book = new Book();
        book.setTitle("Microservices");
        book.setAuthor("Sam");
        book.setUploaded_by(1);
        book.setUploaded_at(String.valueOf(LocalDateTime.now()));
        book.setCategory_id(2);

        when(restTemplate.postForEntity(
                eq(projectUrl + "/rest/v1/books"),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>("{\"message\":\"Book Added\"}", HttpStatus.CREATED));

        String result = supabaseService.addBook(book);
        assertTrue(result.contains("Book Added"));
    }

    @Test
    void testUpdateBook_success() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", "Updated Title");

        when(restTemplate.exchange(
                eq(projectUrl + "/rest/v1/books?book_id=eq.1"),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>("{\"message\":\"Updated\"}", HttpStatus.OK));

        String result = supabaseService.updateBook("1", updates);
        assertTrue(result.contains("Updated"));
    }

    @Test
    void testDeleteBook_success() {
        when(restTemplate.exchange(
                eq(projectUrl + "/rest/v1/books?book_id=eq.1"),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>("{\"message\":\"Deleted\"}", HttpStatus.OK));

        String result = supabaseService.deleteBook("1");
        assertTrue(result.contains("Deleted"));
    }

    @Test
    void testAddCategory_success() {
        BookCategory category = new BookCategory(1, "Science");

        when(restTemplate.postForEntity(
                eq(projectUrl + "/rest/v1/book_categories"),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>("{\"message\":\"Category Added\"}", HttpStatus.CREATED));

        String result = supabaseService.addCategory(category);
        assertTrue(result.contains("Category Added"));
    }

    @Test
    void testUpdateCategory_success() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("category_name", "Updated Category");

        when(restTemplate.exchange(
                eq(projectUrl + "/rest/v1/book_categories?category_id=eq.1"),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>("{\"message\":\"Category Updated\"}", HttpStatus.OK));

        String result = supabaseService.updateCategory("1", updates);
        assertTrue(result.contains("Category Updated"));
    }

    @Test
    void testDeleteCategory_success() {
        when(restTemplate.exchange(
                eq(projectUrl + "/rest/v1/book_categories?category_id=eq.1"),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>("{\"message\":\"Category Deleted\"}", HttpStatus.OK));

        String result = supabaseService.deleteCategory("1");
        assertTrue(result.contains("Category Deleted"));
    }

    @Test
    void testSearchBooks_success() {
        String expected = "[{\"book_id\":1,\"title\":\"Java Programming\"}]";

        when(restTemplate.exchange(
                contains("/rest/v1/books?or="),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));

        String result = supabaseService.searchBooks("Java");
        assertTrue(result.contains("Java Programming"));
    }
}
