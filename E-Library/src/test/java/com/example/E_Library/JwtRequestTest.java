package com.example.E_Library;

import static org.junit.jupiter.api.Assertions.*;

import com.example.E_Library.model.JwtRequest;
import org.junit.jupiter.api.Test;

class JwtRequestTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String expectedUserName = "testuser";
        String expectedPassword = "testpassword123";

        // Act
        JwtRequest jwtRequest = new JwtRequest(expectedUserName, expectedPassword);

        // Assert
        assertEquals(expectedUserName, jwtRequest.getUserName());
        assertEquals(expectedPassword, jwtRequest.getUserPassword());
    }

    @Test
    void testSetters() {
        // Arrange
        JwtRequest jwtRequest = new JwtRequest("initial", "initial");
        String newUserName = "newuser";
        String newPassword = "newpassword456";

        // Act
        jwtRequest.setUserName(newUserName);
        jwtRequest.setUserPassword(newPassword);

        // Assert
        assertEquals(newUserName, jwtRequest.getUserName());
        assertEquals(newPassword, jwtRequest.getUserPassword());
    }

    @Test
    void testConstructorWithNullValues() {
        // Act
        JwtRequest jwtRequest = new JwtRequest(null, null);

        // Assert
        assertNull(jwtRequest.getUserName());
        assertNull(jwtRequest.getUserPassword());
    }

    @Test
    void testSettersWithNullValues() {
        // Arrange
        JwtRequest jwtRequest = new JwtRequest("user", "pass");

        // Act
        jwtRequest.setUserName(null);
        jwtRequest.setUserPassword(null);

        // Assert
        assertNull(jwtRequest.getUserName());
        assertNull(jwtRequest.getUserPassword());
    }

    @Test
    void testConstructorWithEmptyStrings() {
        // Act
        JwtRequest jwtRequest = new JwtRequest("", "");

        // Assert
        assertEquals("", jwtRequest.getUserName());
        assertEquals("", jwtRequest.getUserPassword());
    }

    @Test
    void testSettersWithEmptyStrings() {
        // Arrange
        JwtRequest jwtRequest = new JwtRequest("user", "pass");

        // Act
        jwtRequest.setUserName("");
        jwtRequest.setUserPassword("");

        // Assert
        assertEquals("", jwtRequest.getUserName());
        assertEquals("", jwtRequest.getUserPassword());
    }
}