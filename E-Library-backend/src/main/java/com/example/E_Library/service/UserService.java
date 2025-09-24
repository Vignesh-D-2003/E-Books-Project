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
import com.example.E_Library.exceptions.*;
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
            throw new SupabaseException("Error registering user: "+e.getResponseBodyAsString());
        }
        catch (Exception e) {
            throw new SupabaseException("Unexpected error while registering user: " + e.getMessage());
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
                throw new ResourceNotFoundException("Invalid Password or Email");
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
                return true;
            } else {
                logger.warn("Login failed: Invalid password for email: {}", email);
                // Invalid password
                throw new AuthenticationFailedException("Invalid Pssword or Email");

            }

        } catch (AuthenticationFailedException | ResourceNotFoundException ex) {
            throw ex; // rethrow known exceptions
        } catch (HttpClientErrorException e) {
            logger.error("Supabase API error during login for email {}: {}", email, e.getResponseBodyAsString(), e);
            throw new SupabaseException("Supabase API error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Unexpected error during login for email {}:", email, e);
            throw new SupabaseException("Unexpected error while logging in: " + e.getMessage());
        }
    }


}

