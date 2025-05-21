package com.example.inventory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import com.example.inventory.model.Product;
import com.example.inventory.dto.InventoryMetricsDTO;
import com.example.inventory.dto.ProductDTO;
import com.example.inventory.service.ProductService;
import com.example.inventory.dto.PagedResponse;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Create a product: POST
    @PostMapping()
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        Product product = productService.saveFromDTO(productDTO);
        return ResponseEntity.ok(product);
    }

    // Get all products: GET
    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        PagedResponse<Product> response = productService.getFilteredProducts(
                name, categories, available, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    // Update a product: PUT
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        Optional<Product> existingProductOpt = productService.getProductById(id);

        if (existingProductOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product updatedProduct = productService.updateProduct(id, dto);

        return ResponseEntity.ok(updatedProduct);
    }

    // Mark product as outofstock
    // @PUT outofstock
    @RequestMapping(value = "/{id}/outofstock", method = RequestMethod.POST)
    public ResponseEntity<Product> markProductAsOutOfStock(@PathVariable Long id) {
        Optional<Product> existingProductOpt = productService.getProductById(id);

        if (existingProductOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        productService.markProductAsOutOfStock(id);
        Product updatedProduct = productService.getProductById(id).orElse(null);
        return ResponseEntity.ok(updatedProduct);
    }

    // Mark product as instock
    // @PUT outofstock
    @RequestMapping(value = "/{id}/instock", method = RequestMethod.POST)
    public ResponseEntity<Product> markProductAsInStock(@PathVariable Long id) {
        Optional<Product> existingProductOpt = productService.getProductById(id);

        if (existingProductOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        productService.markProductAsInStock(id);
        Product updatedProduct = productService.getProductById(id).orElse(null);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Optional<Product> existingProductOpt = productService.getProductById(id);

        if (existingProductOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    // Clear all products: DELETE
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearProducts() {
        productService.clear();
        return ResponseEntity.noContent().build();
    }

    // Get all metrics: GET
    @GetMapping("/metrics")
    public ResponseEntity<List<InventoryMetricsDTO>> getInventoryMetrics() {
        List<InventoryMetricsDTO> metrics = productService.getInventoryMetrics();
        return ResponseEntity.ok(metrics);
    }
}
