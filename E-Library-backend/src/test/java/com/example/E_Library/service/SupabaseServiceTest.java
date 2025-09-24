package com.example.E_Library.service;

import com.example.E_Library.exceptions.AuthenticationFailedException;
import com.example.E_Library.exceptions.ResourceNotFoundException;
import com.example.E_Library.model.Book;
import com.example.E_Library.model.BookCategory;
import com.example.E_Library.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class SupabaseServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SupabaseService supabaseService;

    private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;

    private String projectUrl = "https://test.supabase.co";
    private String secretKey = "test-secret-key";
    private String downloadPath = "/tmp/test-downloads";

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up mocked static SecurityContextHolder
        mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class);

        // Use ReflectionTestUtils to set private fields
        ReflectionTestUtils.setField(supabaseService, "projectUrl", projectUrl);
        ReflectionTestUtils.setField(supabaseService, "secretKey", secretKey);
        ReflectionTestUtils.setField(supabaseService, "downloadPath", downloadPath);
        ReflectionTestUtils.setField(supabaseService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(supabaseService, "objectMapper", objectMapper);
    }

    @AfterMethod
    public void tearDown() {
        if (mockedSecurityContextHolder != null) {
            mockedSecurityContextHolder.close();
        }
    }

    // ===== getUserId() Tests =====

    @Test
    public void testGetUserId_Success() throws Exception {
        // Arrange
        String username = "testuser";
        User mockUser = createMockUser();

        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(username);

        String jsonResponse = "[{\"user_id\":1,\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password123\",\"created_at\":\"2023-01-01\",\"updated_at\":\"2023-01-01\",\"is_admin\":false}]";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        JsonNode arrayNode = mock(JsonNode.class);
        JsonNode userNode = mock(JsonNode.class);
        when(objectMapper.readTree(jsonResponse)).thenReturn(arrayNode);
        when(arrayNode.isArray()).thenReturn(true);
        when(arrayNode.size()).thenReturn(1);
        when(arrayNode.get(0)).thenReturn(userNode);

        when(userNode.get("user_id")).thenReturn(mock(JsonNode.class));
        when(userNode.get("user_id").asInt()).thenReturn(1);
        when(userNode.get("username")).thenReturn(mock(JsonNode.class));
        when(userNode.get("username").asText()).thenReturn("testuser");
        when(userNode.get("email")).thenReturn(mock(JsonNode.class));
        when(userNode.get("email").asText()).thenReturn("test@example.com");
        when(userNode.get("password")).thenReturn(mock(JsonNode.class));
        when(userNode.get("password").asText()).thenReturn("password123");
        when(userNode.get("created_at")).thenReturn(mock(JsonNode.class));
        when(userNode.get("created_at").asText()).thenReturn("2023-01-01");
        when(userNode.get("updated_at")).thenReturn(mock(JsonNode.class));
        when(userNode.get("updated_at").asText()).thenReturn("2023-01-01");
        when(userNode.get("is_admin")).thenReturn(mock(JsonNode.class));
        when(userNode.get("is_admin").asBoolean()).thenReturn(false);

        // Act
        Integer userId = supabaseService.getUserId();

        // Assert
        assertEquals(userId, Integer.valueOf(1));
    }

    @Test
    public void testGetUserId_NoAuthentication() {
        // Arrange
        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act & Assert
        try {
            supabaseService.getUserId();
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "No authenticated user found");
        }
    }

    @Test
    public void testGetUserId_NotAuthenticated() {
        // Arrange
        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act & Assert
        try {
            supabaseService.getUserId();
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "No authenticated user found");
        }
    }

    // ===== fetchUserByUsername() Tests =====

    @Test
    public void testFetchUserByUsername_Success() throws Exception {
        // Arrange
        String username = "testuser";
        String jsonResponse = "[{\"user_id\":1,\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password123\",\"created_at\":\"2023-01-01\",\"updated_at\":\"2023-01-01\",\"is_admin\":false}]";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        JsonNode arrayNode = mock(JsonNode.class);
        JsonNode userNode = mock(JsonNode.class);
        when(objectMapper.readTree(jsonResponse)).thenReturn(arrayNode);
        when(arrayNode.isArray()).thenReturn(true);
        when(arrayNode.size()).thenReturn(1);
        when(arrayNode.get(0)).thenReturn(userNode);

        setupUserNodeMocks(userNode);

        // Act
        User user = supabaseService.fetchUserByUsername(username);

        // Assert
        assertNotNull(user);
        assertEquals(user.getUsername(), "testuser");
        assertEquals(user.getEmail(), "test@example.com");
        assertEquals(user.getUser_id(), Integer.valueOf(1));
        assertFalse(user.getIs_admin());
    }

    @Test
    public void testFetchUserByUsername_UserNotFound() throws Exception {
        // Arrange
        String username = "nonexistent";
        String jsonResponse = "[]";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        JsonNode arrayNode = mock(JsonNode.class);
        when(objectMapper.readTree(jsonResponse)).thenReturn(arrayNode);
        when(arrayNode.isArray()).thenReturn(true);
        when(arrayNode.size()).thenReturn(0);

        // Act & Assert
        try {
            supabaseService.fetchUserByUsername(username);
            fail("Expected ResourceNotFoundException to be thrown");
        } catch (ResourceNotFoundException e) {
            assertTrue(e.getMessage().contains("User not found with username: nonexistent"));
        }
    }

    // ===== fetchUserByEmail() Tests =====

    @Test
    public void testFetchUserByEmail_Success() throws Exception {
        // Arrange
        String email = "test@example.com";
        String jsonResponse = "[{\"user_id\":1,\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password123\",\"is_admin\":false}]";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        JsonNode arrayNode = mock(JsonNode.class);
        JsonNode userNode = mock(JsonNode.class);
        when(objectMapper.readTree(jsonResponse)).thenReturn(arrayNode);
        when(arrayNode.isArray()).thenReturn(true);
        when(arrayNode.size()).thenReturn(1);
        when(arrayNode.get(0)).thenReturn(userNode);

        setupBasicUserNodeMocks(userNode);

        // Act
        User user = supabaseService.fetchUserByEmail(email);

        // Assert
        assertNotNull(user);
        assertEquals(user.getEmail(), "test@example.com");
        assertEquals(user.getUsername(), "testuser");
    }

    @Test
    public void testFetchUserByEmail_UserNotFound() throws Exception {
        // Arrange
        String email = "nonexistent@example.com";
        String jsonResponse = "[]";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        JsonNode arrayNode = mock(JsonNode.class);
        when(objectMapper.readTree(jsonResponse)).thenReturn(arrayNode);
        when(arrayNode.isArray()).thenReturn(true);
        when(arrayNode.size()).thenReturn(0);

        // Act & Assert
        try {
            supabaseService.fetchUserByEmail(email);
            fail("Expected AuthenticationFailedException to be thrown");
        } catch (AuthenticationFailedException e) {
            assertEquals(e.getMessage(), "Invalid email or password");
        }
    }

    // ===== addUser() Tests =====

    @Test
    public void testAddUser_Success() {
        // Arrange
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", "newuser");
        userMap.put("email", "newuser@example.com");
        userMap.put("password", "password123");

        String expectedResponse = "{\"message\":\"User created successfully\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.CREATED);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        String result = supabaseService.addUser(userMap);

        // Assert
        assertEquals(result, expectedResponse);
        verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void testAddUser_HttpClientError() {
        // Arrange
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", "existinguser");

        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", "User already exists".getBytes(), null);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(exception);

        // Act & Assert
        try {
            supabaseService.addUser(userMap);
            fail("Expected AuthenticationFailedException to be thrown");
        } catch (AuthenticationFailedException e) {
            assertTrue(e.getMessage().contains("Error registering user"));
        }
    }

    // ===== Book CRUD Tests =====

    @Test
    public void testGetAllBooks_Success() {
        // Arrange
        String expectedResponse = "[{\"book_id\":1,\"title\":\"Test Book\",\"author\":\"Test Author\"}]";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        String result = supabaseService.getAllBooks();

        // Assert
        assertEquals(result, expectedResponse);
    }

    @Test
    public void testGetBookById_Success() {
        // Arrange
        String bookId = "1";
        String expectedResponse = "[{\"book_id\":1,\"title\":\"Test Book\",\"author\":\"Test Author\"}]";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        String result = supabaseService.getBookById(bookId);

        // Assert
        assertEquals(result, expectedResponse);
    }

    @Test
    public void testGetBookById_NotFound() {
        // Arrange
        String bookId = "999";
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenThrow(exception);

        // Act & Assert
        try {
            supabaseService.getBookById(bookId);
            fail("Expected ResourceNotFoundException to be thrown");
        } catch (ResourceNotFoundException e) {
            assertTrue(e.getMessage().contains("Book not found with ID: 999"));
        }
    }

    @Test
    public void testAddBook_Success() {
        // Arrange
        Book book = createMockBook();
        setupAuthenticatedUser();

        String expectedResponse = "{\"book_id\":1,\"title\":\"Test Book\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.CREATED);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        String result = supabaseService.addBook(book);

        // Assert
        assertEquals(result, expectedResponse);
    }

    @Test
    public void testUpdateBook_Success() {
        // Arrange
        String bookId = "1";
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", "Updated Title");

        String expectedResponse = "{\"message\":\"Book updated successfully\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        String result = supabaseService.updateBook(bookId, updates);

        // Assert
        assertEquals(result, expectedResponse);
    }

    @Test
    public void testDeleteBook_Success() {
        // Arrange
        String bookId = "1";
        String expectedResponse = "{\"message\":\"Book deleted successfully\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        String result = supabaseService.deleteBook(bookId);

        // Assert
        assertEquals(result, expectedResponse);
    }

    // ===== Category CRUD Tests =====

    @Test
    public void testGetAllCategories_Success() {
        // Arrange
        String expectedResponse = "[{\"category_id\":1,\"category_name\":\"Fiction\"}]";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        String result = supabaseService.getAllCategories();

        // Assert
        assertEquals(result, expectedResponse);
    }

    @Test
    public void testAddCategory_Success() {
        // Arrange
        BookCategory category = new BookCategory();
        category.setCategory_name("New Category");

        String expectedResponse = "{\"category_id\":1,\"category_name\":\"New Category\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.CREATED);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        String result = supabaseService.addCategory(category);

        // Assert
        assertEquals(result, expectedResponse);
    }

    @Test
    public void testUpdateCategory_Success() {
        // Arrange
        String categoryId = "1";
        Map<String, Object> updates = new HashMap<>();
        updates.put("category_name", "Updated Category");

        String expectedResponse = "{\"message\":\"Category updated successfully\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        String result = supabaseService.updateCategory(categoryId, updates);

        // Assert
        assertEquals(result, expectedResponse);
    }

    @Test
    public void testDeleteCategory_Success() {
        // Arrange
        String categoryId = "1";
        String expectedResponse = "{\"message\":\"Category deleted successfully\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        String result = supabaseService.deleteCategory(categoryId);

        // Assert
        assertEquals(result, expectedResponse);
    }

    // ===== Search Tests =====

    @Test
    public void testSearchBooks_Success() {
        // Arrange
        String query = "java";
        String expectedResponse = "[{\"book_id\":1,\"title\":\"Java Programming\",\"author\":\"Test Author\"}]";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        String result = supabaseService.searchBooks(query);

        // Assert
        assertEquals(result, expectedResponse);
    }

    @Test
    public void testSearchBooks_NotFound() {
        // Arrange
        String query = "nonexistent";
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenThrow(exception);

        // Act & Assert
        try {
            supabaseService.searchBooks(query);
            fail("Expected ResourceNotFoundException to be thrown");
        } catch (ResourceNotFoundException e) {
            assertTrue(e.getMessage().contains("No books found for query: nonexistent"));
        }
    }

    // ===== Helper Methods =====

    private User createMockUser() {
        User user = new User();
        user.setUser_id(1);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setIs_admin(false);
        return user;
    }

    private Book createMockBook() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setDescription("Test Description");
        book.setFile_url("http://example.com/book.pdf");
        book.setCategory_id(1);
        return book;
    }

    private void setupAuthenticatedUser() {
        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");

        // Mock the fetchUserByUsername call in getUserId()
        try {
            String jsonResponse = "[{\"user_id\":1,\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password123\",\"created_at\":\"2023-01-01\",\"updated_at\":\"2023-01-01\",\"is_admin\":false}]";
            ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

            when(restTemplate.exchange(contains("username=eq.testuser"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                    .thenReturn(responseEntity);

            JsonNode arrayNode = mock(JsonNode.class);
            JsonNode userNode = mock(JsonNode.class);
            when(objectMapper.readTree(jsonResponse)).thenReturn(arrayNode);
            when(arrayNode.isArray()).thenReturn(true);
            when(arrayNode.size()).thenReturn(1);
            when(arrayNode.get(0)).thenReturn(userNode);

            setupUserNodeMocks(userNode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setupUserNodeMocks(JsonNode userNode) {
        when(userNode.get("user_id")).thenReturn(mock(JsonNode.class));
        when(userNode.get("user_id").asInt()).thenReturn(1);
        when(userNode.get("username")).thenReturn(mock(JsonNode.class));
        when(userNode.get("username").asText()).thenReturn("testuser");
        when(userNode.get("email")).thenReturn(mock(JsonNode.class));
        when(userNode.get("email").asText()).thenReturn("test@example.com");
        when(userNode.get("password")).thenReturn(mock(JsonNode.class));
        when(userNode.get("password").asText()).thenReturn("password123");
        when(userNode.get("created_at")).thenReturn(mock(JsonNode.class));
        when(userNode.get("created_at").asText()).thenReturn("2023-01-01");
        when(userNode.get("updated_at")).thenReturn(mock(JsonNode.class));
        when(userNode.get("updated_at").asText()).thenReturn("2023-01-01");
        when(userNode.get("is_admin")).thenReturn(mock(JsonNode.class));
        when(userNode.get("is_admin").asBoolean()).thenReturn(false);
    }

    private void setupBasicUserNodeMocks(JsonNode userNode) {
        when(userNode.get("user_id")).thenReturn(mock(JsonNode.class));
        when(userNode.get("user_id").asInt()).thenReturn(1);
        when(userNode.get("username")).thenReturn(mock(JsonNode.class));
        when(userNode.get("username").asText()).thenReturn("testuser");
        when(userNode.get("email")).thenReturn(mock(JsonNode.class));
        when(userNode.get("email").asText()).thenReturn("test@example.com");
        when(userNode.get("password")).thenReturn(mock(JsonNode.class));
        when(userNode.get("password").asText()).thenReturn("password123");
        when(userNode.get("is_admin")).thenReturn(mock(JsonNode.class));
        when(userNode.get("is_admin").asBoolean()).thenReturn(false);
    }
}