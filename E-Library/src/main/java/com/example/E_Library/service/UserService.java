package com.example.E_Library.service;

import java.util.HashMap;
import java.util.Map;

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
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "Error registering user: " + e.getResponseBodyAsString();
        }

    }

    // Login user
    public boolean loginUser(String email, String rawPassword) {
        String url = projectUrl + "/rest/v1/users?email=eq." + email;
        HttpEntity<String> entity = new HttpEntity<>(getHeaders());

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String body = response.getBody();

            if (body == null || body.equals("[]")) return false;

            // Parse user
            JsonNode array = new ObjectMapper().readTree(body);
            JsonNode node = array.get(0);
            String encodedPassword = node.get("password").asText();

            // Compare passwords
            return passwordEncoder.matches(rawPassword, encodedPassword);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

