package com.example.inventory.controller;

import com.example.inventory.model.Category;
import com.example.inventory.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    private CategoryService service;
    private CategoryController controller;

    @BeforeEach
    void setUp() {
        service = mock(CategoryService.class);
        controller = new CategoryController(service);
    }

    @Test
    void createCategory_returnsSavedCategory() {
        Category input = new Category("Category A");
        Category saved = new Category(1L, "Category A");

        when(service.saveCategory(ArgumentMatchers.any(Category.class))).thenReturn(saved);

        ResponseEntity<Category> response = controller.createCategory(input);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(saved, response.getBody());
        verify(service).saveCategory(input);
    }

    @Test
    void getAllActiveCategories_returnsList() {
        Category category1 = new Category(1L, "Category A");
        Category category2 = new Category(2L, "Category B");

        when(service.getAllActiveCategories()).thenReturn(List.of(category1, category2));

        ResponseEntity<List<Category>> response = controller.getAllActiveCategories();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(service).getAllActiveCategories();
    }

    @Test
    void updateCategory_existing_returnsUpdated() {
        Category input = new Category(1L, "Category A");
        Category updated = new Category(1L, "Category B");

        when(service.getCategoryById(1L)).thenReturn(Optional.of(updated));
        when(service.updateCategoryById(1L, input)).thenReturn(updated);

        ResponseEntity<Category> response = controller.updateCategory(1L, input);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(updated, response.getBody());
        verify(service).getCategoryById(1L);
        verify(service).updateCategoryById(1L, input);
    }

    @Test
    void updateCategory_notFound_returnsNotFound() {
        Category input = new Category(1L, "Category A");

        when(service.getCategoryById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Category> response = controller.updateCategory(1L, input);

        assertEquals(404, response.getStatusCode().value());
        verify(service).getCategoryById(1L);
        verify(service, never()).updateCategoryById(anyLong(), any(Category.class));
    }

    @Test
    void deleteCategory_existing_returnsNoContent() {
        Category existing = new Category(1L, "Category A");

        when(service.getCategoryById(1L)).thenReturn(Optional.of(existing));

        ResponseEntity<Void> response = controller.deleteCategory(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(service).getCategoryById(1L);
        verify(service).deleteCategoryById(1L);
    }

    @Test
    void deleteCategory_notFound_returnsNotFound() {
        when(service.getCategoryById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = controller.deleteCategory(1L);

        assertEquals(404, response.getStatusCode().value());
        verify(service).getCategoryById(1L);
        verify(service, never()).deleteCategoryById(anyLong());
    }

    @Test
    void clearCategories_returnsNoContent() {
        ResponseEntity<Void> response = controller.clearCategories();

        assertEquals(204, response.getStatusCode().value());
        verify(service).clearCategories();
    }
}