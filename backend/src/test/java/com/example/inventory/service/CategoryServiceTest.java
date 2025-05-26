package com.example.inventory.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.List;
import java.util.Optional;

import com.example.inventory.model.Category;
import com.example.inventory.repository.CategoryRepository;

class CategoryServiceTest {
    private CategoryRepository repository;
    private CategoryFileStorageService storageService;
    private CategoryService service;

    @BeforeEach
    void setUp() {
        repository = mock(CategoryRepository.class);
        storageService = mock(CategoryFileStorageService.class);
        service = new CategoryService(repository, storageService);
    }

    @Test
    void save_Category_returnsSavedCategory() {
        Category category = new Category("Category A");
        Category saved = new Category(1L, "Category A");

        when(repository.save(any(Category.class))).thenReturn(saved);

        Category result = service.saveCategory(category);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Category A", result.getName());
        verify(repository).save(category);
    }

    @Test
    void save_DuplicatedCategoryName_throwsDuplicatedNameException() {
        Category category = new Category("Category A");

        Category existing = new Category(1L, "Category A");

        when(repository.getAll()).thenReturn(List.of(existing));

        try {
            service.saveCategory(category);
        } catch (IllegalArgumentException e) {
            assertEquals("Category with the same name already exists", e.getMessage());
        }
        verify(repository).getAll();
    }

    @Test
    void updateById_ExistingId_returnsUpdatedCategory() {
        Category existing = new Category(1L, "Category A");

        Category updated = new Category(1L, "Category B");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.updateById(eq(1L), any(Category.class))).thenReturn(updated);

        Category result = service.updateCategoryById(1L, updated);
        assertEquals("Category B", result.getName());
        verify(repository).findById(1L);
       
        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(repository).updateById(eq(1L), captor.capture());
        Category passedToRepository = captor.getValue();
        assertEquals(updated.getName(), passedToRepository.getName());
    }

    @Test
    void findById_FoundId_returnsCategory() {
        Category saved = new Category(1L, "Category A");

        when(repository.findById(1L)).thenReturn(Optional.of(saved));

        Optional<Category> found = service.getCategoryById(1L);
        assertTrue(found.isPresent());
        assertEquals("Category A", found.get().getName());
        verify(repository).findById(1L);
    }

    @Test
    void findById_NotFoundId_returnsEmpty() {
        when(repository.findById(1000L)).thenReturn(Optional.empty());

        Optional<Category> found = service.getCategoryById(1000L);
        assertFalse(found.isPresent());
        verify(repository).findById(1000L);
    }

    @Test
    void deleteById_FoundId_setsActiveFalse() {
        Category existing = new Category(1L, "Category A");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        
        service.deleteCategoryById(1L);

        assertFalse(existing.getActive());
        verify(repository).findById(1L); // Category still exists
    }

    @Test
    void deleteById_NotFound_throwsInvalidIdException() {
        when(repository.findById(1000L)).thenReturn(Optional.empty());

        try {
            service.deleteCategoryById(1000L);
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid category ID", e.getMessage());
        }
        verify(repository).findById(1000L);
    }

    @Test
    void deleteById_AlreadyInactive_throwsDoesNotExistException() {
        Category existing = new Category(1L, "Category A", false);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        try {
            service.deleteCategoryById(1L);
        } catch (IllegalArgumentException e) {
            assertEquals("Category does not exist", e.getMessage());
        }
        verify(repository).findById(1L);
    }

    @Test
    void getAll_returnsAllCategories() {
        Category  category1 = new Category(1L, "Category A");
        Category  category2 = new Category(2L, "Category B");
        
        when(repository.getAll()).thenReturn(List.of( category1,  category2));

        List<Category> allCategories = service.getAllCategories();
        assertEquals(2, allCategories.size());
        assertTrue(allCategories.contains(category1));
        assertTrue(allCategories.contains(category2));
        assertEquals("Category A", allCategories.get(0).getName());
        assertEquals("Category B", allCategories.get(1).getName());
        verify(repository).getAll();
    }
}