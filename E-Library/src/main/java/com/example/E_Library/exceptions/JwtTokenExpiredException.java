package com.example.E_Library.exceptions;

public class JwtTokenExpiredException extends RuntimeException {
    public JwtTokenExpiredException(String message) {
        super(message);
    }
}
