package com.example.E_Library.configuration;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//@Component
// This @Order annotation is the most important part of this file.
// It forces this filter to run FIRST, before any other filter (including the MdcFilter and Spring Security filters).
// This guarantees the traceId is available for the entire request lifecycle.
//@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Generate a new, random, and unique traceId for this request using Java's built-in UUID.
        String traceId = UUID.randomUUID().toString();

        // Put the newly generated traceId into the MDC with the key "traceId".
        MDC.put("traceId", traceId);

        try {
            // Pass the request along to the next filter in the chain (e.g., our MdcFilter).
            filterChain.doFilter(request, response);
        } finally {
            // CRITICAL: After the request is complete, remove the traceId from the MDC.
            // This prevents it from being accidentally used by another request.
            MDC.remove("traceId");
        }
    }
}