package com.example.inventory.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.inventory.model.Product;
import com.example.inventory.repository.ProductRepository;
import com.example.inventory.dto.InventoryMetricsDTO;
import com.example.inventory.dto.ProductDTO;
import com.example.inventory.model.Category;
import com.example.inventory.dto.PagedResponse;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductFileStorageService storageService;

    private static final int DEFAULT_RESTOCK = 10;

    public ProductService(ProductRepository productRepository, CategoryService categoryService,
            ProductFileStorageService storageService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.storageService = storageService;
    }

    public Product saveFromDTO(ProductDTO productDTO) {
        Category category = categoryService.getCategoryById(productDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
        Product product = new Product(
                productDTO.getName(),
                category,
                productDTO.getPrice(),
                productDTO.getStock(),
                productDTO.getExpirationDate());

        Product savedProduct = productRepository.save(product);
        storageService.saveProducts(getAllProducts());
        return savedProduct;
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.getAll().stream()
                .filter(Product::isActive)
                .collect(Collectors.toList());
    }

    public PagedResponse<Product> getFilteredProducts(
            String name, List<Long> categories, String available,
            int page, int size, String sortBy, String sortDirection) {

        List<Product> filtered = productRepository.getAll().stream()
                .filter(Product::isActive)
                .filter(p -> (name == null || p.getName().toLowerCase().contains(name.toLowerCase())) &&
                        (categories == null || categories.isEmpty() ||
                                (p.getCategory() != null && categories.contains(p.getCategory().getId())))
                        &&
                        (available == null || available.isEmpty() ||
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
                        if (p1.getExpirationDate() == null && p2.getExpirationDate() == null)
                            return 0;
                        if (p1.getExpirationDate() == null)
                            return "asc".equalsIgnoreCase(sortDirection) ? 1 : -1;
                        if (p2.getExpirationDate() == null)
                            return "asc".equalsIgnoreCase(sortDirection) ? -1 : 1;
                        return "asc".equalsIgnoreCase(sortDirection)
                                ? p1.getExpirationDate().compareTo(p2.getExpirationDate())
                                : p2.getExpirationDate().compareTo(p1.getExpirationDate());
                    }
                    return 0;
                })
                .collect(Collectors.toList());

        long totalElements = filtered.size();

        List<Product> paged = filtered.stream()
                .skip(page * size)
                .limit(size)
                .collect(Collectors.toList());

        return new PagedResponse<>(paged, totalElements);
    }

    public Optional<Product> getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent() && product.get().isActive()) {
            return product;
        } else {
            return Optional.empty();
        }
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.getAll();
        return products.stream()
                .filter(Product::isActive)
                .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());

    }

    public void deleteProductById(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);

        if (productOptional.isEmpty() || !productOptional.get().isActive()) {
            throw new IllegalArgumentException("Product not found with ID: " + id);
        }

        Product product = productOptional.get();

        product.setActive(false); // Soft delete
        productRepository.updateById(id, product);
        storageService.saveProducts(getAllProducts());
    }

    public Product updateProductById(Long id, ProductDTO productDTO) {
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

        Product updatedProduct = productRepository.updateById(id, existingProduct);
        storageService.saveProducts(getAllProducts());

        return updatedProduct;
    }

    public void markProductAsOutOfStock(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));
        product.setStock(0);
        productRepository.updateById(id, product);
        storageService.saveProducts(getAllProducts());
    }

    public void markProductAsInStock(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));
        product.setStock(DEFAULT_RESTOCK);
        productRepository.updateById(id, product);
        storageService.saveProducts(getAllProducts());
    }

    public void clearProducts() {
        productRepository.clear();
        storageService.clear();
    }

    public List<InventoryMetricsDTO> getInventoryMetrics() {
        List<Product> allProducts = getAllProducts().stream()
                .filter(p -> p.getStock() > 0)
                .collect(Collectors.toList());

        Map<String, List<Product>> groupedByCategory = allProducts.stream()
                .collect(Collectors.groupingBy(p -> p.getCategory().getName()));

        List<InventoryMetricsDTO> metrics = new ArrayList<>();

        for (Map.Entry<String, List<Product>> entry : groupedByCategory.entrySet()) {
            String categoryName = entry.getKey();
            List<Product> productsInCategory = entry.getValue();

            double totalStock = productsInCategory.stream()
                    .mapToDouble(Product::getStock)
                    .sum();

            double totalValue = productsInCategory.stream()
                    .mapToDouble(p -> p.getPrice() * p.getStock())
                    .sum();

            double averagePrice = productsInCategory.stream()
                    .mapToDouble(Product::getPrice)
                    .average()
                    .orElse(0.0);

            InventoryMetricsDTO metric = new InventoryMetricsDTO(categoryName, totalStock, totalValue, averagePrice);
            metrics.add(metric);
        }

        // Overall metrics
        double overallTotalStock = allProducts.stream()
                .mapToDouble(Product::getStock)
                .sum();
        double overallTotalValue = allProducts.stream()
                .mapToDouble(p -> p.getPrice() * p.getStock())
                .sum();
        double overallAveragePrice = allProducts.stream()
                .mapToDouble(Product::getPrice)
                .average()
                .orElse(0.0);

        InventoryMetricsDTO overallMetrics = new InventoryMetricsDTO("Overall", overallTotalStock, overallTotalValue,
                overallAveragePrice);

        metrics.add(overallMetrics);

        return metrics;
    }
}
