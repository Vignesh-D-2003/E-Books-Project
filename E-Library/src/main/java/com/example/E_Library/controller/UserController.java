package com.example.E_Library.controller;
import com.example.E_Library.model.JwtRequest;
import com.example.E_Library.model.JwtResponse;
import com.example.E_Library.model.User;
import com.example.E_Library.service.JwtService;
import com.example.E_Library.service.SupabaseService;
import com.example.E_Library.service.UserService;


import java.util.Map;

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

	@Autowired
	private PasswordEncoder passwordEncoder;
	private AuthenticationManager authenticationManager;
	private JwtService jwtService;
	private SupabaseService supabaseService;
	
	private User user;
    @Autowired
    private UserService userService;

    // Register
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return  userService.registerUser(user);
    }
    
    // Login
//    @PostMapping("/login")
//    public String login(@RequestBody Map<String, String> newUser) {
//        String email = newUser.get("email");
//        String password = newUser.get("password");
//        
//        boolean success = userService.loginUser(email, password);
//        return success ? "Login successful" : "Invalid credentials";
//    }
    
    @Autowired
    public  UserController(AuthenticationManager authenticationManager, JwtService jwtService , SupabaseService supabaseService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.supabaseService=supabaseService;
        
    }

   
    @PostMapping("/login")
    public JwtResponse login(@RequestBody Map<String, String> loginRequest) throws Exception {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        // 1️⃣ Check credentials via Supabase (old logic)
        boolean validUser = userService.loginUser(email, password);
        if (!validUser) {
            throw new BadCredentialsException("Invalid credentials");
        }
        
       

        // 2️⃣ Fetch full user info for JWT (needed for roles)
        User user = supabaseService.fetchUserByEmail(email); 
        if (user == null) {
            throw new BadCredentialsException("Invalid email credentials"); // stops null usage
        }
        // Implement this in UserService
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password credentials");
        }
        // 3️⃣ Authenticate via Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), password)
        );

        // 4️⃣ Generate JWT token
        JwtRequest jwtRequest = new JwtRequest(user.getUsername(), password);
        return jwtService.createJwtToken(jwtRequest);
    }

}

