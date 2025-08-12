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

    public CategoryService(final CategoryRepository repository) {
        this.categoryRepository = repository;
    }

    /**
     * Saves a new category to the repository.
     *
     * @param category the category to save
     * @return the saved category
     * @throws IllegalArgumentException if a category with the same name already exists
     */
    public Category saveCategory(final Category category) {
        if (categoryRepository.getAll().stream().filter(cat -> cat.isActive())
                .anyMatch(existingCategory -> existingCategory.getName().equalsIgnoreCase(category.getName()))) {
            throw new IllegalArgumentException(DUPLICATE_CATEGORY_NAME);
        }

        Category savedCategory = categoryRepository.save(category);
        return savedCategory;
    }

    /**
     * Retrieves all active categories from the repository.
     *
     * @return list of all active categories
     */
    public List<Category> getAllActiveCategories() {
        return categoryRepository.getAll().stream()
                .filter(Category::isActive)
                .toList();
    }

    /**
     * Retrieves all categories (both active and inactive) from the repository.
     *
     * @return list of all categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.getAll();
    }

    /**
     * Retrieves an active category by its ID.
     *
     * @param id the ID of the category to retrieve
     * @return optional containing the category if found and active, empty otherwise
     */
    public Optional<Category> getCategoryById(final Long id) {
        return categoryRepository.findById(id)
                .filter(Category::isActive);
    }

    /**
     * Updates an existing category by ID.
     *
     * @param id the ID of the category to update
     * @param category the updated category data
     * @return the updated category
     * @throws NotFoundException if the category is not found or inactive
     * @throws IllegalArgumentException if a category with the same name already exists
     */
    public Category updateCategoryById(final Long id, final Category category) {
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

    /**
     * Deletes a category by marking it as inactive (soft delete).
     *
     * @param id the ID of the category to delete
     * @throws NotFoundException if the category is not found or already inactive
     */
    public void deleteCategoryById(final Long id) {
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

    /**
     * Clears all categories from the repository.
     */
    public void clearCategories() {
        categoryRepository.clear();
    }
}
