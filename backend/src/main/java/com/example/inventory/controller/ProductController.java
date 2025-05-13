package com.example.inventory.controller;

import com.example.inventory.dto.ProductUpdateDTO;
import com.example.inventory.model.Product;
import com.example.inventory.model.Category;
import com.example.inventory.service.ProductService;
import com.example.inventory.service.CategoryService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // Create a product: POST
    @PostMapping()
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product saved = productService.saveProduct(product);
        return ResponseEntity.ok(saved);
    }

    // Get all products: GET
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // Update a product: PUT
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateDTO dto) {
        Optional<Product> existingProductOpt = productService.getProductById(id);

        if (existingProductOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product existingProduct = existingProductOpt.get();

        // From req: Update a product (name, category, price, stock, expiration date).
        existingProduct.setName(dto.getName());
        existingProduct.setPrice(dto.getPrice());
        existingProduct.setStock(dto.getStock());
        existingProduct.setExpirationDate(dto.getExpirationDate());

        if (dto.getCategoriaId() != null) {
            Category category = categoryService.getCategoryById(dto.getCategoriaId()).orElse(null);

            existingProduct.setCategory(category);
        }

        Product updatedProduct = productService.saveProduct(existingProduct);

        return ResponseEntity.ok(updatedProduct);
    }

    // Mark product as outofstock
    // @POST outofstock

    // Mark product as instock
    // @PUT outofstock
}
