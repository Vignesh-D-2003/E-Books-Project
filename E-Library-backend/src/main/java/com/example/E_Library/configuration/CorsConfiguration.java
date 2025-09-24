package com.example.E_Library.configuration;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Component
public class CorsConfiguration {
	private static final String GET = "GET";
	private static final String POST = "POST";
	private static final String DELETE = "DELETE";
	private static final String PUT = "PUT";
	
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
				.allowedMethods(GET,POST,PUT,DELETE)
				.allowedHeaders("*")
				.allowedOriginPatterns("*")
				.allowCredentials(true)
				;
			}
		};
		
	}

}
