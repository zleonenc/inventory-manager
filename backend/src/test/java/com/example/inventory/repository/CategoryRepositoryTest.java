package com.example.inventory.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import com.example.inventory.model.Category;

class CategoryRepositoryTest {
    private CategoryRepository repository;

    @BeforeEach
    void setUp() {
        repository = new CategoryRepository();
    }

    @Test
    void save_Category_returnsSavedCategory() {
        Category category = new Category("Category A");
        Category saved = repository.save(category);
      
        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        assertEquals("Category A", saved.getName());
    }

    @Test
    void updateById_ExistingId_returnsUpdatedCategory() {
        Category category = new Category("Category A");
        Category saved = repository.save(category);

        saved.setName("Category B");
        Category updated = repository.updateById(saved.getId(), saved);

        assertEquals("Category B", updated.getName());
    }

    @Test
    void findById_FoundId_returnsCategory() {
        Category category = new Category("Category A");
        Category saved = repository.save(category);

        Optional<Category> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Category A", found.get().getName());
    }

    @Test
    void findById_NotFoundId_returnsEmpty() {
        Optional<Category> found = repository.findById(1000L);
        assertFalse(found.isPresent());
    }

    @Test
    void deleteById_FoundId_returnsTrue() {
        Category category = new Category("Category A");
        Category saved = repository.save(category);

        boolean deleted = repository.deleteById(saved.getId());
        assertTrue(deleted);
        assertFalse(repository.findById(saved.getId()).isPresent());
    }

    @Test
    void deleteById_NotFound_returnsFalse() {
        boolean deleted = repository.deleteById(1000L);
        assertFalse(deleted);
    }

    @Test
    void getAll_returnsAllCategories() {
        Category category1 = new Category("Category A");
        Category category2 = new Category("Category B");
        repository.save(category1);
        repository.save(category2);

        List<Category> allCategories = repository.getAll();
        assertEquals(2, allCategories.size());
        assertTrue(allCategories.contains(category1));
        assertTrue(allCategories.contains(category2));
        assertEquals("Category A", allCategories.get(0).getName());
        assertEquals("Category B", allCategories.get(1).getName());
    }

    @Test
    void clear_removesAllCategories() {
        Category category1 = new Category("Category A");
        repository.save(category1);

        repository.clear();
        assertTrue(repository.getAll().isEmpty());
    }

    @Test
    void loadCategories_loadsCategoriesAndUpdatesIdGenerator() {
        Category category1 = new Category("Category A");
        Category category2 = new Category("Category B");
        repository.loadCategories(List.of(category1, category2));

        List<Category> allCategories = repository.getAll();
        
        assertEquals(2, allCategories.size());
        assertTrue(allCategories.contains(category1));
        assertTrue(allCategories.contains(category2));
        assertEquals(1L, allCategories.get(0).getId());
        assertEquals(2L, allCategories.get(1).getId());
        assertEquals("Category A", allCategories.get(0).getName());
        assertEquals("Category B", allCategories.get(1).getName());

        assertEquals(2, repository.getIdGenerator().get());
    }
}
