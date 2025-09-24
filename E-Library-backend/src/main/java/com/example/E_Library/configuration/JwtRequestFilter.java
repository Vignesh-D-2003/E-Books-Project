package com.example.E_Library.configuration;

import java.io.IOException;
import com.example.E_Library.exceptions.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.E_Library.service.JwtService;
import com.example.E_Library.utils.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class JwtRequestFilter extends OncePerRequestFilter{
	private final JwtService jwtService;
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(JwtService jwtService, JwtUtil jwtUtil) {
        this.jwtService = jwtService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        final String header = request.getHeader("Authorization");
        String jwtToken = null;
        String userName = null;

        if (header != null && header.startsWith("Bearer ")) {
            jwtToken = header.substring(7);

            try {
                userName = jwtUtil.getUserNameFromToken(jwtToken);
            } catch (InvalidJwtException e) {
                System.out.println("Unable to get the jwtToken");
            } catch (JwtTokenExpiredException e) {
                throw new JwtTokenExpiredException("Jwt Token Expired");
            }
        }

        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = jwtService.loadUserByUsername(userName);

            if (jwtUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null,
                                userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
	

}
