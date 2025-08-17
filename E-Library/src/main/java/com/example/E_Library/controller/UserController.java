package com.example.E_Library.controller;
import com.example.E_Library.model.JwtRequest;
import com.example.E_Library.model.JwtResponse;
import com.example.E_Library.model.User;
import com.example.E_Library.service.JwtService;
import com.example.E_Library.service.SupabaseService;
import com.example.E_Library.service.UserService;


import java.util.Map;

// Import the SLF4J Logger classes
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    // 1. Create a logger instance for this class.
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private SupabaseService supabaseService;

    // 'user' field is not used and can be removed.
    // private User user;

    @Autowired
    private UserService userService;

    @Autowired
    public  UserController(AuthenticationManager authenticationManager, JwtService jwtService , SupabaseService supabaseService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.supabaseService=supabaseService;
    }

    // Register
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        // 2. Add logging, being careful NOT to log the password from the user object.
        logger.info("Request received to register a new user with email: {}", user.getEmail());
        return  userService.registerUser(user);
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody Map<String, String> loginRequest) throws Exception {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password"); // We will only use this for validation, NEVER log it.

        logger.info("Login attempt received for email: {}", email);

        try {
            // 1️⃣ Check credentials via Supabase (old logic)
            boolean validUser = userService.loginUser(email, password);
            if (!validUser) {
                // This specific reason is already logged in the service, but we add a controller-level warning.
                logger.warn("Login failed for email '{}' due to invalid credentials (checked by user service).", email);
                throw new BadCredentialsException("Invalid credentials");
            }
            logger.debug("Initial credential check passed for email: {}", email);

            // 2️⃣ Fetch full user info for JWT (needed for roles)
            User user = supabaseService.fetchUserByEmail(email);
            if (user == null) {
                // This case should ideally not be hit if validUser is true, but it's good defensive coding.
                logger.error("Logic error: User service validated user '{}', but could not be fetched for JWT.", email);
                throw new BadCredentialsException("Invalid email credentials");
            }
            // Password check is already done inside userService.loginUser, but this re-confirms.
            if (!passwordEncoder.matches(password, user.getPassword())) {
                logger.warn("Login failed for email '{}': password mismatch after fetching full user profile.", email);
                throw new BadCredentialsException("Invalid password credentials");
            }

            // 3️⃣ Authenticate via Spring Security
            logger.debug("Authenticating user '{}' with Spring Security's AuthenticationManager.", user.getUsername());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), password)
            );
            logger.debug("Spring Security authentication successful for user: {}", user.getUsername());

            // 4️⃣ Generate JWT token
            JwtRequest jwtRequest = new JwtRequest(user.getUsername(), password);
            JwtResponse response = jwtService.createJwtToken(jwtRequest);
            logger.info("JWT token generated successfully for user: {}", user.getUsername());
            return response;

        } catch (BadCredentialsException e) {
            // Catching the exception to log it before it's sent to the client.
            logger.warn("Authentication failed for email {}: {}", email, e.getMessage());
            throw e; // Re-throw the exception so the client gets the correct 401/403 response.
        } catch (Exception e) {
            // Catch any other unexpected errors during login.
            logger.error("An unexpected error occurred during login for email: {}", email, e);
            throw e;
        }
    }
}