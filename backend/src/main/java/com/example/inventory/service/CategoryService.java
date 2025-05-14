package com.example.inventory.service;

import com.example.inventory.model.Category;
import com.example.inventory.repository.CategoryRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository productRepository;

    public CategoryService(CategoryRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Category saveCategory(Category product) {
        return productRepository.save(product);
    }

    public List<Category> getAllCategorys() {
        return productRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return productRepository.findById(id);
    }

    public boolean deleteCategory(Long id) {
        return productRepository.deleteById(id);
    }
}
