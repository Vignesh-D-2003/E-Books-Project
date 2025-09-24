package com.example.E_Library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

import com.example.E_Library.configuration.TraceIdFilter;

@SpringBootApplication
public class ELibraryApplication {
    private static final Logger logger = LoggerFactory.getLogger(ELibraryApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ELibraryApplication.class, args);
        logger.info("ELibrary Application has started successfully. Log file should be generated.");
    }

    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilterRegistration() {
        // Create a registration bean for our TraceIdFilter
        FilterRegistrationBean<TraceIdFilter> registrationBean = new FilterRegistrationBean<>();

        // Add the filter to the registration bean
        registrationBean.setFilter(new TraceIdFilter());

        // Set the order to the highest possible precedence to ensure it runs first
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return registrationBean;
    }

}
