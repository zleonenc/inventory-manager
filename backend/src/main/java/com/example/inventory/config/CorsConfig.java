package com.example.inventory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins:http://localhost:8080}")
    private String allowedOrigins;

    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${app.cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                String[] origins = StringUtils.commaDelimitedListToStringArray(allowedOrigins);
                String[] methods = StringUtils.commaDelimitedListToStringArray(allowedMethods);
                String[] headers = StringUtils.commaDelimitedListToStringArray(allowedHeaders);

                registry.addMapping("/api/**")
                        .allowedOriginPatterns(origins)
                        .allowedMethods(methods)
                        .allowedHeaders(headers)
                        .allowCredentials(allowCredentials);
            }
        };
    }
}
