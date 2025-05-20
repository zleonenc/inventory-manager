package com.example.inventory.service;

import com.example.inventory.model.Category;
import com.example.inventory.repository.CategoryRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        storageService.saveCategories(getAllActiveCategories());
        return savedCategory;
    }

    public List<Category> getAllActiveCategories() {
        return categoryRepository.getAll().stream()
                .filter(Category::getActive)
                .toList();
    }

    public List<Category> getAllCategories() {
        return categoryRepository.getAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id)
            .filter(Category::getActive);
    }

    public Category updateCategory(Long id, Category category) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        if (!existingCategory.getActive()) {
            throw new IllegalArgumentException("Category does not exist");
        }

        existingCategory.setName(category.getName());
        existingCategory.setUpdateDate(LocalDate.now());

        Category updatedCategory = categoryRepository.save(existingCategory);
        storageService.saveCategories(getAllCategories());
        return updatedCategory;
    }

    public void deleteCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        if (!category.getActive()) {
            throw new IllegalArgumentException("Category does not exist");
        }else{
            category.setActive(false);
            storageService.saveCategories(getAllCategories());
        }
    }

    public void clearCategories() {
        categoryRepository.clear();
        storageService.clear();
    }
}
