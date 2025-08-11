package com.example.inventory.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.example.inventory.config.ErrorMessages.*;
import com.example.inventory.exception.NotFoundException;
import com.example.inventory.repository.CategoryRepository;
import com.example.inventory.model.Category;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category saveCategory(Category category) {
        if (categoryRepository.getAll().stream().filter(cat -> cat.isActive())
                .anyMatch(existingCategory -> existingCategory.getName().equalsIgnoreCase(category.getName()))) {
            throw new IllegalArgumentException(DUPLICATE_CATEGORY_NAME);
        }

        Category savedCategory = categoryRepository.save(category);
        return savedCategory;
    }

    public List<Category> getAllActiveCategories() {
        return categoryRepository.getAll().stream()
                .filter(Category::isActive)
                .toList();
    }

    public List<Category> getAllCategories() {
        return categoryRepository.getAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .filter(Category::isActive);
    }

    public Category updateCategoryById(Long id, Category category) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(CATEGORY_NOT_FOUND, id)));

        if (categoryRepository.getAll().stream().filter(cat -> cat.isActive())
                .anyMatch(existingCategoryAux -> existingCategoryAux.getName().equalsIgnoreCase(category.getName())
                        && !existingCategoryAux.getId().equals(id))) {
            throw new IllegalArgumentException(DUPLICATE_CATEGORY_NAME);
        }

        if (!existingCategory.isActive()) {
            throw new NotFoundException(String.format(CATEGORY_NOT_FOUND, id));
        }

        existingCategory.setName(category.getName());
        existingCategory.setUpdateDate(LocalDate.now());

        Category updatedCategory = categoryRepository.updateById(id, existingCategory);
        return updatedCategory;
    }

    public void deleteCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(CATEGORY_NOT_FOUND, id)));

        if (!category.isActive()) {
            throw new NotFoundException(String.format(CATEGORY_NOT_FOUND, id));
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
