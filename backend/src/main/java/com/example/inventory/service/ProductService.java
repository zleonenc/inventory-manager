package com.example.inventory.service;

import com.example.inventory.model.Product;
import com.example.inventory.repository.ProductRepository;
import com.example.inventory.dto.ProductDTO;
import com.example.inventory.model.Category;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductFileStorageService storageService;

    private static final int DEFAULT_RESTOCK = 10;

    public ProductService(ProductRepository productRepository, CategoryService categoryService, ProductFileStorageService storageService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.storageService = storageService;
    }

    public Product saveFromDTO(ProductDTO productDTO) {
        Category category = categoryService.getCategoryById(productDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
        Product product = new Product(
                null,
                productDTO.getName(),
                category,
                productDTO.getPrice(),
                productDTO.getStock(),
                productDTO.getExpirationDate()  
        );

        Product savedProduct = productRepository.save(product);
        storageService.saveProducts(getAllProducts());
        return savedProduct;
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.getAll();
    }

    public List<Product> getFilteredProducts(String name, List<Long> categories, String available, int page, int size, String sortBy, String sortDirection) {
        List<Product> products = productRepository.getAll();

        products = products.stream()
            .filter(Product::isActive)
            .filter(p -> 
            (name == null || p.getName().toLowerCase().contains(name.toLowerCase())) &&
            (categories == null || categories.isEmpty() || 
                (p.getCategory() != null && categories.contains(p.getCategory().getId()))) &&
            (available == null || 
                ("instock".equalsIgnoreCase(available) && p.getStock() > 0) || 
                ("outofstock".equalsIgnoreCase(available) && p.getStock() == 0)))
            .sorted((p1, p2) -> {
            if ("name".equalsIgnoreCase(sortBy)) {
            return "asc".equalsIgnoreCase(sortDirection) 
                ? p1.getName().compareTo(p2.getName()) 
                : p2.getName().compareTo(p1.getName());
            } else if ("price".equalsIgnoreCase(sortBy)) {
            return "asc".equalsIgnoreCase(sortDirection) 
                ? Double.compare(p1.getPrice(), p2.getPrice()) 
                : Double.compare(p2.getPrice(), p1.getPrice());
            } else if ("category".equalsIgnoreCase(sortBy)) {
            String category1 = p1.getCategory() != null ? p1.getCategory().getName() : "";
            String category2 = p2.getCategory() != null ? p2.getCategory().getName() : "";
            return "asc".equalsIgnoreCase(sortDirection) 
                ? category1.compareTo(category2) 
                : category2.compareTo(category1);
            } else if ("stock".equalsIgnoreCase(sortBy)) {
            return "asc".equalsIgnoreCase(sortDirection) 
                ? Double.compare(p1.getStock(), p2.getStock()) 
                : Double.compare(p2.getStock(), p1.getStock());
            } else if ("expirationdate".equalsIgnoreCase(sortBy)) {
            return "asc".equalsIgnoreCase(sortDirection) 
                ? p1.getExpirationDate().compareTo(p2.getExpirationDate()) 
                : p2.getExpirationDate().compareTo(p1.getExpirationDate());
            }
            return 0;
            })
            .skip(page * size)
            .limit(size)
            .collect(Collectors.toList());
        
        return products;
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public void deleteProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));
        
        product.setActive(false); // Mark the product as inactive
        productRepository.save(product);
        storageService.saveProducts(getAllProducts());
    }

    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));

        Category category = categoryService.getCategoryById(productDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        existingProduct.setName(productDTO.getName());
        existingProduct.setCategory(category);
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setStock(productDTO.getStock());
        existingProduct.setExpirationDate(productDTO.getExpirationDate());
        existingProduct.setUpdateDate(LocalDate.now());

        storageService.saveProducts(getAllProducts());
        return productRepository.save(existingProduct);
    }

    public void markProductAsOutOfStock(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));
        product.setStock(0);
        productRepository.save(product);
        storageService.saveProducts(getAllProducts());
    }

    public void markProductAsInStock(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));
        product.setStock(DEFAULT_RESTOCK);
        productRepository.save(product);
        storageService.saveProducts(getAllProducts());
    }

    public void clear() {
        productRepository.clear();
        storageService.clear();
    }
}
