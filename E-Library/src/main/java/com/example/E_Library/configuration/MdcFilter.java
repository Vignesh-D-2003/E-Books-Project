package com.example.E_Library.configuration;

import java.io.IOException;
import java.util.stream.Collectors;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MdcFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Try to get the authenticated user from Spring Security's context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if a user is present, authenticated, and is a type we can use
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Put the user's principal name (usually username or email) into the MDC.
            MDC.put("userId", userDetails.getUsername());

            // Get all authorities/roles, remove the "ROLE_" prefix for cleaner logs, and join them.
            String roles = userDetails.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                    .collect(Collectors.joining(","));

            MDC.put("userRoles", roles);
        }

        try {
            // Continue processing the request
            filterChain.doFilter(request, response);
        } finally {
            // CRITICAL: Always clear the MDC after the request is finished
            // to ensure the user data doesn't leak into another request's logs.
            MDC.clear();
        }
    }
}
