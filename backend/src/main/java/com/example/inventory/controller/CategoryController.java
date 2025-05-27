package com.example.inventory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import com.example.inventory.model.Category;
import com.example.inventory.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Create a category: POST
    @PostMapping()
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        Category saved = categoryService.saveCategory(category);
        return ResponseEntity.ok(saved);
    }

    // Get all active categories: GET
    @GetMapping
    public ResponseEntity<List<Category>> getAllActiveCategories() {
        return ResponseEntity.ok(categoryService.getAllActiveCategories());
    }

    // Update a category: PUT
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Optional<Category> existingCategoryOpt = categoryService.getCategoryById(id);

        if (existingCategoryOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Category updatedCategory = categoryService.updateCategoryById(id, category);
        return ResponseEntity.ok(updatedCategory);
    }

    // Mark a category as inactive: DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        Optional<Category> existingCategoryOpt = categoryService.getCategoryById(id);

        if (existingCategoryOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        categoryService.deleteCategoryById(id);

        return ResponseEntity.noContent().build();
    }

    // Clear all categories: DELETE
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCategories() {
        categoryService.clearCategories();
        return ResponseEntity.noContent().build();
    }
}
