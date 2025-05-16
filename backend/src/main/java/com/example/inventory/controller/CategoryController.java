package com.example.inventory.controller;

import com.example.inventory.model.Category;
import com.example.inventory.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

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

    // Get all categories: GET
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // Update a category: PUT
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Optional<Category> existingCategoryOpt = categoryService.getCategoryById(id);

        if (existingCategoryOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Category existingCategory = existingCategoryOpt.get();
        existingCategory.setName(category.getName());
        existingCategory.setActive(category.getActive());

        Category updated = categoryService.saveCategory(existingCategory);
        return ResponseEntity.ok(updated);
    }

    // Mark a category as inactive: DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        Optional<Category> existingCategoryOpt = categoryService.getCategoryById(id);

        if (existingCategoryOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Category existingCategory = existingCategoryOpt.get();
        existingCategory.setActive(false);
        categoryService.saveCategory(existingCategory);

        return ResponseEntity.noContent().build();
    }

    // Clear all categories: DELETE
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCategories() {
        categoryService.clearCategories();
        return ResponseEntity.noContent().build();
    }
}
