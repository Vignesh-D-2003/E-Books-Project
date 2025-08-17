package com.example.E_Library;

import static org.junit.jupiter.api.Assertions.*;

import com.example.E_Library.model.JwtResponse;
import com.example.E_Library.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtResponseTest {

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setIs_admin(false);
    }

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

        // Act
        JwtResponse jwtResponse = new JwtResponse(testUser, expectedToken);

        // Assert
        assertEquals(testUser, jwtResponse.getUser());
        assertEquals(expectedToken, jwtResponse.getJwtToken());
    }

    @Test
    void testSetters() {
        // Arrange
        JwtResponse jwtResponse = new JwtResponse(testUser, "initial-token");

        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        String newToken = "new-jwt-token-123";

        // Act
        jwtResponse.setUser(newUser);
        jwtResponse.setJwtToken(newToken);

        // Assert
        assertEquals(newUser, jwtResponse.getUser());
        assertEquals("newuser", jwtResponse.getUser().getUsername());
        assertEquals("new@example.com", jwtResponse.getUser().getEmail());
        assertEquals(newToken, jwtResponse.getJwtToken());
    }

    @Test
    void testConstructorWithNullValues() {
        // Act
        JwtResponse jwtResponse = new JwtResponse(null, null);

        // Assert
        assertNull(jwtResponse.getUser());
        assertNull(jwtResponse.getJwtToken());
    }

    @Test
    void testSettersWithNullValues() {
        // Arrange
        JwtResponse jwtResponse = new JwtResponse(testUser, "token");

        // Act
        jwtResponse.setUser(null);
        jwtResponse.setJwtToken(null);

        // Assert
        assertNull(jwtResponse.getUser());
        assertNull(jwtResponse.getJwtToken());
    }

    @Test
    void testConstructorWithNullUser() {
        // Arrange
        String token = "valid-jwt-token";

        // Act
        JwtResponse jwtResponse = new JwtResponse(null, token);

        // Assert
        assertNull(jwtResponse.getUser());
        assertEquals(token, jwtResponse.getJwtToken());
    }

    @Test
    void testConstructorWithNullToken() {
        // Act
        JwtResponse jwtResponse = new JwtResponse(testUser, null);

        // Assert
        assertEquals(testUser, jwtResponse.getUser());
        assertNull(jwtResponse.getJwtToken());
    }

    @Test
    void testConstructorWithEmptyToken() {
        // Act
        JwtResponse jwtResponse = new JwtResponse(testUser, "");

        // Assert
        assertEquals(testUser, jwtResponse.getUser());
        assertEquals("", jwtResponse.getJwtToken());
    }

    @Test
    void testUserObjectConsistency() {
        // Arrange
        String token = "test-token";
        JwtResponse jwtResponse = new JwtResponse(testUser, token);

        // Act - modify the original user object
        testUser.setUsername("modified-username");

        // Assert - the response should reflect the change since it's the same object reference
        assertEquals("modified-username", jwtResponse.getUser().getUsername());
        assertEquals(testUser, jwtResponse.getUser());
    }

    @Test
    void testSetUserWithDifferentUserObject() {
        // Arrange
        JwtResponse jwtResponse = new JwtResponse(testUser, "token");

        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setIs_admin(true);

        // Act
        jwtResponse.setUser(adminUser);

        // Assert
        assertEquals(adminUser, jwtResponse.getUser());
        assertEquals("admin", jwtResponse.getUser().getUsername());
        assertEquals("admin@example.com", jwtResponse.getUser().getEmail());
        assertTrue(jwtResponse.getUser().getIs_admin());

        // Original user should be unchanged
        assertEquals("testuser", testUser.getUsername());
        assertEquals("test@example.com", testUser.getEmail());
        assertFalse(testUser.getIs_admin());
    }
}