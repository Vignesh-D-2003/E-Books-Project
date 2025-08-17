package com.example.E_Library;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import com.example.E_Library.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.E_Library.model.User;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private String projectUrl = "https://test-project.supabase.co";
    private String secretKey = "test-secret-key";

    @BeforeEach
    void setUp() {
        // Set the @Value fields using ReflectionTestUtils
        ReflectionTestUtils.setField(userService, "projectUrl", projectUrl);
        ReflectionTestUtils.setField(userService, "secretKey", secretKey);
        ReflectionTestUtils.setField(userService, "restTemplate", restTemplate);

        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("rawPassword123");
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        String encodedPassword = "encodedPassword123";
        String expectedResponse = "{\"id\":1,\"username\":\"testuser\"}";

        when(passwordEncoder.encode("rawPassword123")).thenReturn(encodedPassword);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.CREATED));

        // Act
        String result = userService.registerUser(testUser);

        // Assert
        assertEquals(expectedResponse, result);

        verify(passwordEncoder, times(1)).encode("rawPassword123");
        verify(restTemplate, times(1)).postForEntity(
                eq(projectUrl + "/rest/v1/users"),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void testRegisterUser_HttpClientErrorException() {
        // Arrange
        String encodedPassword = "encodedPassword123";
        String errorResponse = "{\"error\":\"User already exists\"}";

        when(passwordEncoder.encode("rawPassword123")).thenReturn(encodedPassword);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorResponse.getBytes(), null));

        // Act
        String result = userService.registerUser(testUser);

        // Assert
        assertTrue(result.startsWith("Error registering user:"));
        assertTrue(result.contains(errorResponse));

        verify(passwordEncoder, times(1)).encode("rawPassword123");
        verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void testLoginUser_Success() {
        // Arrange
        String email = "test@example.com";
        String rawPassword = "rawPassword123";
        String encodedPassword = "encodedPassword123";
        String responseBody = "[{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"" + encodedPassword + "\"}]";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        // Act
        boolean result = userService.loginUser(email, rawPassword);

        // Assert
        assertTrue(result);

        verify(restTemplate, times(1)).exchange(
                eq(projectUrl + "/rest/v1/users?email=eq." + email),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        );
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }

    @Test
    void testLoginUser_InvalidPassword() {
        // Arrange
        String email = "test@example.com";
        String rawPassword = "wrongPassword";
        String encodedPassword = "encodedPassword123";
        String responseBody = "[{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"" + encodedPassword + "\"}]";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        // Act
        boolean result = userService.loginUser(email, rawPassword);

        // Assert
        assertFalse(result);

        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }

    @Test
    void testLoginUser_UserNotFound_EmptyArray() {
        // Arrange
        String email = "nonexistent@example.com";
        String rawPassword = "password123";
        String responseBody = "[]";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        // Act
        boolean result = userService.loginUser(email, rawPassword);

        // Assert
        assertFalse(result);

        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testLoginUser_UserNotFound_NullResponse() {
        // Arrange
        String email = "nonexistent@example.com";
        String rawPassword = "password123";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        // Act
        boolean result = userService.loginUser(email, rawPassword);

        // Assert
        assertFalse(result);

        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testLoginUser_ExceptionHandling() {
        // Arrange
        String email = "test@example.com";
        String rawPassword = "password123";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Network error"));

        // Act
        boolean result = userService.loginUser(email, rawPassword);

        // Assert
        assertFalse(result);

        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testLoginUser_JsonParsingError() {
        // Arrange
        String email = "test@example.com";
        String rawPassword = "password123";
        String invalidJson = "invalid json response";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(invalidJson, HttpStatus.OK));

        // Act
        boolean result = userService.loginUser(email, rawPassword);

        // Assert
        assertFalse(result);

        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}