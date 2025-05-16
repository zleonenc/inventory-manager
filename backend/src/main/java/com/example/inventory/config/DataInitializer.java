package com.example.inventory.config;

import com.example.inventory.model.Category;
import com.example.inventory.service.CategoryFileStorageService;
import com.example.inventory.repository.CategoryRepository;

import com.example.inventory.model.Product;
import com.example.inventory.service.ProductFileStorageService;
import com.example.inventory.repository.ProductRepository;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductFileStorageService productFileStorageService;
    private final CategoryFileStorageService categoryFileStorageService;

    public DataInitializer(ProductRepository productRepository, CategoryRepository categoryRepository,
                           ProductFileStorageService productFileStorageService,
                           CategoryFileStorageService categoryFileStorageService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productFileStorageService = productFileStorageService;
        this.categoryFileStorageService = categoryFileStorageService;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Category> categories = categoryFileStorageService.loadCategories();
        categories.forEach(categoryRepository::save);
        
        List<Product> products = productFileStorageService.loadProducts();
        products.forEach(productRepository::save);

        System.out.println("Loaded " + categories.size() + " categories and " + products.size() + " products.");
    }
}

