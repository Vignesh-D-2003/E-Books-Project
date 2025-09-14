package com.example.E_Library.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.Map;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.example.E_Library.exceptions.AuthenticationFailedException;
import com.example.E_Library.exceptions.ResourceNotFoundException;
import com.example.E_Library.exceptions.SupabaseException;
import com.example.E_Library.model.User;

public class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserService userService;

    private static final String PROJECT_URL = "https://test.supabase.co";
    private static final String SECRET_KEY = "test-secret-key";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$10$encodedPassword";

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inject the mock values using ReflectionTestUtils
        ReflectionTestUtils.setField(userService, "projectUrl", PROJECT_URL);
        ReflectionTestUtils.setField(userService, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(userService, "restTemplate", restTemplate);
    }

    @AfterMethod
    public void tearDown() {
        // Reset mocks after each test
        reset(passwordEncoder, restTemplate);
    }

    // ===== registerUser() Tests =====

    @Test
    public void testRegisterUser_Success() {
        // Arrange
        User user = createTestUser();
        String expectedResponse = "{\"id\":1,\"username\":\"testuser\",\"email\":\"test@example.com\"}";

        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.CREATED));

        // Act
        String result = userService.registerUser(user);

        // Assert
        assertEquals(result, expectedResponse);
        verify(passwordEncoder).encode(TEST_PASSWORD);
        verify(restTemplate).postForEntity(
                eq(PROJECT_URL + "/rest/v1/users"),
                argThat((HttpEntity<?> entity) -> { // <-- FIX
                    @SuppressWarnings("unchecked")
                    Map<String, Object> body = (Map<String, Object>) entity.getBody();
                    return body.get("username").equals(TEST_USERNAME) &&
                            body.get("email").equals(TEST_EMAIL) &&
                            body.get("password").equals(ENCODED_PASSWORD) &&
                            body.get("is_admin").equals(false);
                }),
                eq(String.class)
        );
    }

    // ... (rest of the tests are fine until the headers test)

    // ===== Headers Tests =====

    @Test
    public void testGetHeaders_CorrectHeadersSet() {
        // Arrange
        User user = createTestUser();

        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("success", HttpStatus.CREATED));

        // Act
        userService.registerUser(user);

        // Assert - verify that the headers were set correctly in the HTTP entity
        verify(restTemplate).postForEntity(
                anyString(),
                argThat((HttpEntity<?> entity) -> { // <-- FIX
                    HttpHeaders headers = entity.getHeaders();
                    return headers.getContentType().equals(MediaType.APPLICATION_JSON) &&
                            headers.get("apikey").contains(SECRET_KEY) &&
                            headers.get("Authorization").contains("Bearer " + SECRET_KEY);
                }),
                eq(String.class)
        );
    }


    @Test
    public void testRegisterUser_SpecialCharactersInUsername() {
        // Arrange
        User user = createTestUser();
        user.setUsername("test_user-123");

        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("{\"id\":1}", HttpStatus.CREATED));

        // Act
        String result = userService.registerUser(user);

        // Assert
        assertNotNull(result);
        verify(restTemplate).postForEntity(
                anyString(),
                argThat((HttpEntity<Map<String, Object>> entity) -> { // <-- FIX with more specific typing
                    if (entity == null) return false;
                    Map<String, Object> body = entity.getBody();
                    return body != null && body.get("username").equals("test_user-123");
                }),
                eq(String.class)
        );
    }

    // ===== Helper Methods =====

    private User createTestUser() {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setEmail(TEST_EMAIL);
        user.setPassword(TEST_PASSWORD);
        return user;
    }
}