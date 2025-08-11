package com.example.inventory.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import java.util.List;

import static com.example.inventory.testutil.builders.CategoryBuilder.aCategory;
import com.example.inventory.exception.NotFoundException;
import com.example.inventory.service.CategoryService;
import com.example.inventory.model.Category;

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
        Category input = aCategory().withId(null).build();
        Category saved = aCategory().withId(1L).build();

        when(service.saveCategory(ArgumentMatchers.any(Category.class))).thenReturn(saved);

        ResponseEntity<Category> response = controller.createCategory(input);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(saved, response.getBody());
        verify(service).saveCategory(input);
    }

    @Test
    void getAllActiveCategories_returnsList() {
        Category category1 = aCategory().withId(1L).build();
        Category category2 = aCategory().withId(2L).build();

        when(service.getAllActiveCategories()).thenReturn(List.of(category1, category2));

        ResponseEntity<List<Category>> response = controller.getAllActiveCategories();

        assertEquals(200, response.getStatusCode().value());
        List<Category> body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.size());
        verify(service).getAllActiveCategories();
    }

    @Test
    void updateCategory_existing_returnsUpdated() {
        Category input = aCategory().withId(1L).build();
        Category updated = aCategory().withId(1L).withName("Category B").build();

        when(service.updateCategoryById(1L, input)).thenReturn(updated);

        ResponseEntity<Category> response = controller.updateCategory(1L, input);

        assertEquals(200, response.getStatusCode().value());
        Category body = response.getBody();
        assertNotNull(body);
        assertEquals(updated, body);
        verify(service).updateCategoryById(1L, input);
    }

    @Test
    void updateCategory_NotFound_throwsNotFoundException() {
        Category input = aCategory().withId(1L).build();
        when(service.updateCategoryById(1L, input)).thenThrow(new NotFoundException("not found"));

        assertThrows(NotFoundException.class, () -> controller.updateCategory(1L, input));
        verify(service).updateCategoryById(1L, input);
    }

    @Test
    void deleteCategory_existing_returnsNoContent() {
        ResponseEntity<Void> response = controller.deleteCategory(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(service).deleteCategoryById(1L);
    }

    @Test
    void deleteCategory_NotFound_throwsNotFoundException() {
        doThrow(new NotFoundException("not found")).when(service).deleteCategoryById(1L);

        assertThrows(NotFoundException.class, () -> controller.deleteCategory(1L));
        verify(service).deleteCategoryById(1L);
    }

    @Test
    void clearCategories_returnsNoContent() {
        ResponseEntity<Void> response = controller.clearCategories();

        assertEquals(204, response.getStatusCode().value());
        verify(service).clearCategories();
    }
}