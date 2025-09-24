package com.example.E_Library.service;

import com.example.E_Library.exceptions.*;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.E_Library.model.JwtRequest;
import com.example.E_Library.model.JwtResponse;
import com.example.E_Library.model.User;
import com.example.E_Library.utils.JwtUtil;

@Service
public class JwtService implements UserDetailsService {

    private final SupabaseService supabaseService;
    private final JwtUtil jwtUtil;

    public JwtService(SupabaseService supabaseService, JwtUtil jwtUtil) {
        this.supabaseService = supabaseService;
        this.jwtUtil = jwtUtil;
    }

    public JwtResponse createJwtToken(JwtRequest jwtRequest) throws Exception {
        String userName = jwtRequest.getUserName();

        final UserDetails userDetails = loadUserByUsername(userName);
        String newGeneratedToken = jwtUtil.generateToken(userDetails);

        User user = supabaseService.fetchUserByUsername(userName);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with username: " + userName);
        }

        return new JwtResponse(user, newGeneratedToken);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = supabaseService.fetchUserByUsername(username);
        if (user == null) {
            throw new AuthenticationFailedException("Invalid username or password");
        }

                 

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                getAuthorities(user)
        );
    }

    private Set<SimpleGrantedAuthority> getAuthorities(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        if (Boolean.TRUE.equals(user.getIs_admin())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return authorities;
    }
}
