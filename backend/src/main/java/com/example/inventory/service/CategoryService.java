package com.example.inventory.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.inventory.model.Category;
import com.example.inventory.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category saveCategory(Category category) {
        if (categoryRepository.getAll().stream().filter(cat -> cat.getActive())
                .anyMatch(existingCategory -> existingCategory.getName().equalsIgnoreCase(category.getName()))) {
            throw new IllegalArgumentException("Category with the same name already exists");
        }

        Category savedCategory = categoryRepository.save(category);
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

    public Category updateCategoryById(Long id, Category category) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        if (categoryRepository.getAll().stream().filter(cat -> cat.getActive())
                .anyMatch(existingCategoryAux -> existingCategoryAux.getName().equalsIgnoreCase(category.getName())
                        && !existingCategoryAux.getId().equals(id))) {
            throw new IllegalArgumentException("Category with the same name already exists");
        }

        if (!existingCategory.getActive()) {
            throw new IllegalArgumentException("Category does not exist");
        }

        existingCategory.setName(category.getName());
        existingCategory.setUpdateDate(LocalDate.now());

        Category updatedCategory = categoryRepository.updateById(id, existingCategory);
        return updatedCategory;
    }

    public void deleteCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        if (!category.getActive()) {
            throw new IllegalArgumentException("Category does not exist");
        } else {
            category.setActive(false);
            category.setUpdateDate(LocalDate.now());
            categoryRepository.updateById(id, category);
        }
    }

    public void clearCategories() {
        categoryRepository.clear();
    }
}
