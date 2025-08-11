package com.example.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springdoc.core.models.GroupedOpenApi;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventory Manager API")
                        .version("v1")
                        .description("API for managing products, categories, and inventory metrics")
                        .contact(new Contact().name("Inventory Team")));
    }

    @Bean
    public GroupedOpenApi productsGroup() {
        return GroupedOpenApi.builder()
                .group("products")
                .pathsToMatch("/api/products/**")
                .build();
    }

    @Bean
    public GroupedOpenApi categoriesGroup() {
        return GroupedOpenApi.builder()
                .group("categories")
                .pathsToMatch("/api/categories/**")
                .build();
    }
}
