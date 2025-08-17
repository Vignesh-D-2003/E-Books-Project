package com.example.E_Library.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 📌 Utility method to return plain string message
    private ResponseEntity<String> buildResponse(HttpStatus status, String message) {
        return new ResponseEntity<>(message, status);
    }

    // ========== Custom Exceptions ==========

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<String> handleInvalidJwt(InvalidJwtException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(JwtTokenExpiredException.class)
    public ResponseEntity<String> handleJwtExpired(JwtTokenExpiredException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "JWT token has expired. Please login again.");
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<String> handleAuthenticationFailed(AuthenticationFailedException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(SupabaseException.class)
    public ResponseEntity<String> handleSupabaseException(SupabaseException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Supabase error: " + ex.getMessage());
    }

    // ========== Built-in Exceptions ==========

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        // ⚠️ Log the detailed exception for debugging
        ex.printStackTrace();
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }
}
