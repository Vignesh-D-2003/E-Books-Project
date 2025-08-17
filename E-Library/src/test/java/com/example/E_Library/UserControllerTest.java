package com.example.E_Library;

import com.example.E_Library.controller.UserController;
import com.example.E_Library.model.JwtRequest;
import com.example.E_Library.model.JwtResponse;
import com.example.E_Library.model.User;
import com.example.E_Library.service.JwtService;
import com.example.E_Library.service.SupabaseService;
import com.example.E_Library.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private SupabaseService supabaseService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserController userController;

    private ObjectMapper objectMapper;

    private User testUser;
    private JwtResponse testJwtResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        // Manually create UserController with mocked dependencies
        userController = new UserController(authenticationManager, jwtService, supabaseService);

        // Use reflection to set the other dependencies that are @Autowired
        try {
            java.lang.reflect.Field userServiceField = UserController.class.getDeclaredField("userService");
            userServiceField.setAccessible(true);
            userServiceField.set(userController, userService);

            java.lang.reflect.Field passwordEncoderField = UserController.class.getDeclaredField("passwordEncoder");
            passwordEncoderField.setAccessible(true);
            passwordEncoderField.set(userController, passwordEncoder);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject dependencies", e);
        }

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice() // Add this for proper exception handling
                .build();

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword123");

        testJwtResponse = new JwtResponse(testUser, "mock-jwt-token");
    }

    // ===== REGISTRATION TESTS =====

    @Test
    void testRegister_Success() throws Exception {
        // Arrange
        when(userService.registerUser(any(User.class))).thenReturn("User registered successfully");

        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        verify(userService, times(1)).registerUser(any(User.class));
    }

    @Test
    void testRegister_InvalidInput() throws Exception {
        // Arrange
        User invalidUser = new User();
        // Empty user object to test validation

        when(userService.registerUser(any(User.class))).thenReturn("Registration failed: Invalid input");

        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registration failed: Invalid input"));

        verify(userService, times(1)).registerUser(any(User.class));
    }

    @Test
    void testRegister_UserAlreadyExists() throws Exception {
        // Arrange
        when(userService.registerUser(any(User.class))).thenReturn("User already exists");

        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("User already exists"));

        verify(userService, times(1)).registerUser(any(User.class));
    }

    // ===== LOGIN TESTS =====

    @Test
    void testLogin_Success() throws Exception {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "test@example.com");
        loginRequest.put("password", "password123");

        when(userService.loginUser("test@example.com", "password123")).thenReturn(true);
        when(supabaseService.fetchUserByEmail("test@example.com")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("testuser", "password123"));
        when(jwtService.createJwtToken(any(JwtRequest.class))).thenReturn(testJwtResponse);

        // Act & Assert
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").value("mock-jwt-token"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.user.username").value("testuser"));

        verify(userService, times(1)).loginUser("test@example.com", "password123");
        verify(supabaseService, times(1)).fetchUserByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword123");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).createJwtToken(any(JwtRequest.class));
    }

    // ===== TESTING EXCEPTIONS USING DIRECT CONTROLLER CALLS =====
    // Since MockMvc is not handling exceptions as expected, let's test the controller directly

    @Test
    void testLogin_InvalidCredentials_UserServiceFails() {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "test@example.com");
        loginRequest.put("password", "wrongpassword");

        when(userService.loginUser("test@example.com", "wrongpassword")).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            userController.login(loginRequest);
        });

        verify(userService, times(1)).loginUser("test@example.com", "wrongpassword");
        verify(supabaseService, never()).fetchUserByEmail(anyString());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void testLogin_UserNotFound_AfterValidation() {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "test@example.com");
        loginRequest.put("password", "password123");

        when(userService.loginUser("test@example.com", "password123")).thenReturn(true);
        when(supabaseService.fetchUserByEmail("test@example.com")).thenReturn(null);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            userController.login(loginRequest);
        });

        verify(userService, times(1)).loginUser("test@example.com", "password123");
        verify(supabaseService, times(1)).fetchUserByEmail("test@example.com");
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void testLogin_PasswordMismatch_AfterFetch() {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "test@example.com");
        loginRequest.put("password", "password123");

        when(userService.loginUser("test@example.com", "password123")).thenReturn(true);
        when(supabaseService.fetchUserByEmail("test@example.com")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            userController.login(loginRequest);
        });

        verify(userService, times(1)).loginUser("test@example.com", "password123");
        verify(supabaseService, times(1)).fetchUserByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword123");
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void testLogin_AuthenticationManagerFails() throws Exception {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "test@example.com");
        loginRequest.put("password", "password123");

        when(userService.loginUser("test@example.com", "password123")).thenReturn(true);
        when(supabaseService.fetchUserByEmail("test@example.com")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Authentication failed"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            userController.login(loginRequest);
        });

        verify(userService, times(1)).loginUser("test@example.com", "password123");
        verify(supabaseService, times(1)).fetchUserByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword123");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).createJwtToken(any());
    }

    @Test
    void testLogin_DisabledException() throws Exception {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "test@example.com");
        loginRequest.put("password", "password123");

        when(userService.loginUser("test@example.com", "password123")).thenReturn(true);
        when(supabaseService.fetchUserByEmail("test@example.com")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new DisabledException("Account disabled"));

        // Act & Assert
        assertThrows(DisabledException.class, () -> {
            userController.login(loginRequest);
        });

        verify(userService, times(1)).loginUser("test@example.com", "password123");
        verify(supabaseService, times(1)).fetchUserByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword123");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).createJwtToken(any());
    }

    @Test
    void testLogin_MissingEmail() {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("password", "password123");
        // Missing email - this will be null

        // Act & Assert
        assertThrows(Exception.class, () -> {
            userController.login(loginRequest);
        });

        verify(userService, never()).loginUser(anyString(), anyString());
    }

    @Test
    void testLogin_MissingPassword() {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "test@example.com");
        // Missing password - this will be null

        // Act & Assert
        assertThrows(Exception.class, () -> {
            userController.login(loginRequest);
        });

        verify(userService, never()).loginUser(anyString(), anyString());
    }

    @Test
    void testLogin_EmptyRequestBody() {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        // Empty request body

        // Act & Assert
        assertThrows(Exception.class, () -> {
            userController.login(loginRequest);
        });

        verify(userService, never()).loginUser(anyString(), anyString());
    }

    @Test
    void testLogin_UnexpectedException() {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "test@example.com");
        loginRequest.put("password", "password123");

        when(userService.loginUser("test@example.com", "password123"))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userController.login(loginRequest);
        });

        verify(userService, times(1)).loginUser("test@example.com", "password123");
        verify(supabaseService, never()).fetchUserByEmail(anyString());
    }

    @Test
    void testLogin_DirectCall_Success() throws Exception {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "test@example.com");
        loginRequest.put("password", "password123");

        when(userService.loginUser("test@example.com", "password123")).thenReturn(true);
        when(supabaseService.fetchUserByEmail("test@example.com")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("testuser", "password123"));
        when(jwtService.createJwtToken(any(JwtRequest.class))).thenReturn(testJwtResponse);

        // Act
        JwtResponse response = userController.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getJwtToken());
        assertEquals("test@example.com", response.getUser().getEmail());
        assertEquals("testuser", response.getUser().getUsername());

        verify(userService, times(1)).loginUser("test@example.com", "password123");
        verify(supabaseService, times(1)).fetchUserByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword123");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).createJwtToken(any(JwtRequest.class));
    }

    @Test
    void testRegister_DirectCall_Success() {
        // Arrange
        when(userService.registerUser(any(User.class))).thenReturn("User registered successfully");

        // Act
        String result = userController.register(testUser);

        // Assert
        assertEquals("User registered successfully", result);
        verify(userService, times(1)).registerUser(any(User.class));
    }
}