package com.example.inventory.service;

import org.springframework.stereotype.Service;

import com.example.inventory.config.JacksonConfig;
import com.example.inventory.model.Product;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProductFileStorageService {
    private final ObjectMapper objectMapper = new JacksonConfig().objectMapper();
    private final Path filePath = Paths.get("./backend/src/main/resources/products.json");

    public void saveProducts(List<Product> products) {
        try {
            objectMapper.writeValue(filePath.toFile(), products);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save products to file", e);
        }
    }

    public List<Product> loadProducts() {
        try {
            if (filePath.toFile().exists()) {
                return objectMapper.readValue(filePath.toFile(), objectMapper.getTypeFactory().constructCollectionType(List.class, Product.class));
            } else {
                return List.of(); // Return an empty list if the file does not exist
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load products from file", e);
        }
    }

    public void clear() {
        try {
            if (filePath.toFile().exists()) {
                filePath.toFile().delete();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear products file", e);
        }
    }
}
