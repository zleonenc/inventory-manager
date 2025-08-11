package com.example.inventory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.ProblemDetail;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import com.example.inventory.service.CategoryService;
import com.example.inventory.model.Category;

@RestController
@RequestMapping("/api/categories")
@Validated
@Tag(name = "Categories", description = "Operations related to categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Create a category: POST
    @PostMapping()
    @Operation(summary = "Create a new category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category created"),
            @ApiResponse(responseCode = "400", description = "Invalid category payload", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        Category saved = categoryService.saveCategory(category);
        return ResponseEntity.ok(saved);
    }

    // Get all active categories: GET
    @GetMapping
    @Operation(summary = "Get all active categories")
    public ResponseEntity<List<Category>> getAllActiveCategories() {
        return ResponseEntity.ok(categoryService.getAllActiveCategories());
    }

    // Update a category: PUT
    @PutMapping("/{id}")
    @Operation(summary = "Update a category by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "400", description = "Invalid category payload", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody Category category) {
        Category updatedCategory = categoryService.updateCategoryById(id, category);
        return ResponseEntity.ok(updatedCategory);
    }

    // Mark a category as inactive: DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete (mark inactive) a category by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.noContent().build();
    }

    // Clear all categories: DELETE
    @DeleteMapping("/clear")
    @Operation(summary = "Delete all categories")
    public ResponseEntity<Void> clearCategories() {
        categoryService.clearCategories();
        return ResponseEntity.noContent().build();
    }
}
