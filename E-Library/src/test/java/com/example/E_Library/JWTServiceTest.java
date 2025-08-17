package com.example.E_Library;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collection;

import com.example.E_Library.service.JwtService;
import com.example.E_Library.service.SupabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.E_Library.model.JwtRequest;
import com.example.E_Library.model.JwtResponse;
import com.example.E_Library.model.User;
import com.example.E_Library.utils.JwtUtil;

@ExtendWith(MockitoExtension.class)
class JWTServiceTest {

    @Mock
    private SupabaseService supabaseService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private JwtService jwtService;

    private User testUser;
    private User testAdmin;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setIs_admin(false);

        testAdmin = new User();
        testAdmin.setUsername("admin");
        testAdmin.setPassword("adminpass");
        testAdmin.setIs_admin(true);
    }

    @Test
    void testCreateJwtToken_Success() throws Exception {
        // Arrange
        JwtRequest jwtRequest = new JwtRequest("testuser", "password123");

        String expectedToken = "mock-jwt-token";

        when(supabaseService.fetchUserByUsername("testuser")).thenReturn(testUser);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn(expectedToken);

        // Act
        JwtResponse response = jwtService.createJwtToken(jwtRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testUser, response.getUser());
        assertEquals(expectedToken, response.getJwtToken());

        verify(supabaseService, times(2)).fetchUserByUsername("testuser");
        verify(jwtUtil, times(1)).generateToken(any(UserDetails.class));
    }

    @Test
    void testCreateJwtToken_UserNotFound() {
        // Arrange
        JwtRequest jwtRequest = new JwtRequest("nonexistent", "password");

        when(supabaseService.fetchUserByUsername("nonexistent")).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            jwtService.createJwtToken(jwtRequest);
        });

        verify(supabaseService, times(1)).fetchUserByUsername("nonexistent");
        verify(jwtUtil, never()).generateToken(any(UserDetails.class));
    }

    @Test
    void testLoadUserByUsername_RegularUser() {
        // Arrange
        when(supabaseService.fetchUserByUsername("testuser")).thenReturn(testUser);

        // Act
        UserDetails userDetails = jwtService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

        verify(supabaseService, times(1)).fetchUserByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_AdminUser() {
        // Arrange
        when(supabaseService.fetchUserByUsername("admin")).thenReturn(testAdmin);

        // Act
        UserDetails userDetails = jwtService.loadUserByUsername("admin");

        // Assert
        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("adminpass", userDetails.getPassword());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));

        verify(supabaseService, times(1)).fetchUserByUsername("admin");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(supabaseService.fetchUserByUsername("nonexistent")).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            jwtService.loadUserByUsername("nonexistent");
        });

        verify(supabaseService, times(1)).fetchUserByUsername("nonexistent");
    }
}