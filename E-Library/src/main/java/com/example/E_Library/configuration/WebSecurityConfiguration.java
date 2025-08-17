package com.example.E_Library.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.E_Library.service.JwtService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity 
public class WebSecurityConfiguration {
	 private final JwtRequestFilter jwtRequestFilter;
	    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	    private final JwtService jwtService;

	    public WebSecurityConfiguration(JwtRequestFilter jwtRequestFilter,
	                                    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
	                                    JwtService jwtService) {
	        this.jwtRequestFilter = jwtRequestFilter;
	        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
	        this.jwtService = jwtService;
	    }

	    // ✅ Define password encoder
	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

	    // ✅ AuthenticationProvider replacing AuthenticationManagerBuilder
	    @Bean
	    public AuthenticationProvider authenticationProvider() {
	        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder());
	        provider.setUserDetailsService(jwtService); // JwtService implements UserDetailsService
	        return provider;
	    }


	    // ✅ AuthenticationManager bean 
	    @Bean
	    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
	        return config.getAuthenticationManager();
	    }

	    // ✅ SecurityFilterChain instead of configure(HttpSecurity)
	    @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http
	            .csrf(csrf -> csrf.disable())
	            .cors(cors -> {}) // enable CORS
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/users/register","/users/login").permitAll() // public 
	                .requestMatchers(HttpHeaders.ALLOW).permitAll()
	                .anyRequest().authenticated()
	            )
	            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
	            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

	        // Add JWT filter
	        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

	        return http.build();
	    }

}
