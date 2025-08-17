package com.example.E_Library.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.E_Library.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.client.HttpClientErrorException;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${PROJECT_URL}")
    private String projectUrl;

	 @Value("${SECRET_KEY}")
	 private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // Helper headers
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", secretKey);
        headers.set("Authorization", "Bearer " + secretKey);
        return headers;
    }

    // Register user
    public String registerUser(User user) {
        String url = projectUrl + "/rest/v1/users"; // "users" table in DB
        Map<String, Object> newUser = new HashMap<>();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        newUser.put("username", user.getUsername());
        newUser.put("email", user.getEmail());
        newUser.put("password", encodedPassword);
        newUser.put("is_admin", false);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(newUser,getHeaders());

        try {
            logger.info("Sending registration request to Supabase for user: {}", user.getEmail());
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            logger.info("Successfully registered user: {}", user.getEmail());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            // Log the error with details from the exception
            logger.error("Error during user registration for email {}: Status Code: {}, Response: {}",
                    user.getEmail(), e.getStatusCode(), e.getResponseBodyAsString(), e);
            return "Error registering user: " + e.getResponseBodyAsString();
        }

    }

    // Login user
    public boolean loginUser(String email, String rawPassword) {
        logger.debug("Login attempt for email: {}", email);
        String url = projectUrl + "/rest/v1/users?email=eq." + email;
        HttpEntity<String> entity = new HttpEntity<>(getHeaders());

        try {
            logger.info("Fetching user details from Supabase for email: {}", email);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String body = response.getBody();

            if (body == null || body.equals("[]")) {
                logger.warn("Login failed: No user found for email: {}", email);
                return false;
            }

            // Parse user
            JsonNode array = new ObjectMapper().readTree(body);
            JsonNode node = array.get(0);
            String encodedPassword = node.get("password").asText();

            // Compare passwords
            // Compare passwords
            boolean passwordMatches = passwordEncoder.matches(rawPassword, encodedPassword);
            if (passwordMatches) {
                logger.info("Login successful for email: {}", email);
            } else {
                logger.warn("Login failed: Invalid password for email: {}", email);
            }
            return passwordMatches;

        } catch (Exception e) {
            logger.error("An unexpected error occurred during login for email {}:", email, e);
            // We are no longer using e.printStackTrace(); the logger handles it.
            return false;
        }
    }

}

