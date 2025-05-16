package com.example.inventory.service;

import com.example.inventory.model.Category;
import com.example.inventory.repository.CategoryRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryFileStorageService storageService;

    public CategoryService(CategoryRepository categoryRepository, CategoryFileStorageService storageService) {
        this.categoryRepository = categoryRepository;
        this.storageService = storageService;
    }

    public Category saveCategory(Category category) {
        Category savedCategory = categoryRepository.save(category);
        storageService.saveCategories(getAllCategories());
        return savedCategory;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public boolean deleteCategoryById(Long id) {
        return categoryRepository.deleteById(id);
    }

    public void clearCategories() {
        categoryRepository.clear();
        storageService.clear();
    }
}
