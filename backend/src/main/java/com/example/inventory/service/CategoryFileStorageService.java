package com.example.inventory.service;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.example.inventory.config.JacksonConfig;
import com.example.inventory.model.Category;

@Service
public class CategoryFileStorageService {
    private final ObjectMapper objectMapper = new JacksonConfig().objectMapper();
    private final Path filePath = Paths.get("./backend/src/main/resources/categories.json");
    /*
     * private final Path filePath =
     * Paths.get("./src/main/resources/categories.json");
     */

    public void saveCategories(List<Category> categories) {
        try {
            objectMapper.writeValue(filePath.toFile(), categories);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save categories to file", e);
        }
    }

    public List<Category> loadCategories() {
        try {
            if (filePath.toFile().exists()) {
                return objectMapper.readValue(filePath.toFile(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Category.class));
            } else {
                return List.of(); // Return an empty list if the file does not exist
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load categories from file", e);
        }
    }

    public void clear() {
        try {
            if (filePath.toFile().exists()) {
                filePath.toFile().delete();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear categories file", e);
        }
    }
}
