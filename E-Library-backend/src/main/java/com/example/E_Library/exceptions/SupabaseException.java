package com.example.E_Library.exceptions;

public class SupabaseException extends RuntimeException {
    public SupabaseException(String message) {
        super(message);
    }

    public SupabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
