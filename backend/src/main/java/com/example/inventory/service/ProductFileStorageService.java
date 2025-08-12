package com.example.inventory.service;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.example.inventory.config.JacksonConfig;
import com.example.inventory.model.Product;

@Service
public class ProductFileStorageService {
    private final ObjectMapper objectMapper = new JacksonConfig().objectMapper();
    private final Path filePath = Paths.get("./src/main/resources/products.json");

    /**
     * Saves a list of products to the JSON file.
     *
     * @param products the list of products to save
     * @throws RuntimeException if saving to file fails
     */
    public void saveProducts(final List<Product> products) {
        try {
            objectMapper.writeValue(filePath.toFile(), products);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save products to file", e);
        }
    }

    /**
     * Loads products from the JSON file.
     *
     * @return list of products loaded from file, or empty list if file doesn't exist
     * @throws RuntimeException if loading from file fails
     */
    public List<Product> loadProducts() {
        try {
            if (filePath.toFile().exists()) {
                return objectMapper.readValue(filePath.toFile(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Product.class));
            } else {
                return List.of(); // Return an empty list if the file does not exist
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load products from file", e);
        }
    }

    /**
     * Clears the products file by deleting it.
     *
     * @throws RuntimeException if clearing the file fails
     */
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
