package com.example.E_Library.controller;
import com.example.E_Library.model.User;
import com.example.E_Library.service.UserService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    // Register
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return  userService.registerUser(user);
    }
    
    // Login
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> newUser) {
        String email = newUser.get("email");
        String password = newUser.get("password");
        
        boolean success = userService.loginUser(email, password);
        return success ? "Login successful" : "Invalid credentials";
    }

}

