package com.example.inventory.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
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
            int page, int size, String primarySortBy, String primarySortDirection,
            String secondarySortBy, String secondarySortDirection) {

        // Filtering
        List<Product> filteredProductsList = productRepository.getAll().stream()
                .filter(Product::isActive)
                .filter(p -> (name == null || p.getName().toLowerCase().contains(name.toLowerCase())) &&
                        (categories == null || categories.isEmpty() ||
                                (p.getCategory() != null && categories.contains(p.getCategory().getId())))
                        &&
                        (available == null || available.isEmpty() ||
                                ("instock".equalsIgnoreCase(available) && p.getStock() > 0) ||
                                ("outofstock".equalsIgnoreCase(available) && p.getStock() == 0)))
                .collect(Collectors.toList());

        // Sorting
        Comparator<Product> finalComparator = createComparator(primarySortBy, primarySortDirection);
        if (secondarySortBy != null && !secondarySortBy.isEmpty()) {
            finalComparator = finalComparator.thenComparing(createComparator(secondarySortBy, secondarySortDirection));
        }

        List<Product> sortedProducts = filteredProductsList.stream()
                                          .sorted(finalComparator)
                                          .collect(Collectors.toList());

        long totalElements = sortedProducts.size();

        List<Product> paged = sortedProducts.stream()
                .skip((long)page * size)
                .limit(size)
                .collect(Collectors.toList());

        return new PagedResponse<>(paged, totalElements);
    }

    private Comparator<Product> createComparator(String sortByField, String direction) {
        if (sortByField == null || sortByField.isEmpty()) {
            return (p1, p2) -> 0;
        }

        boolean ascending = (direction == null || "asc".equalsIgnoreCase(direction));
        Comparator<Product> fieldComparator;

        switch (sortByField.toLowerCase()) {
            case "name":
                fieldComparator = Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
                break;
            case "price":
                fieldComparator = Comparator.comparingDouble(Product::getPrice);
                break;
            case "category":
                fieldComparator = Comparator.comparing(p -> (p.getCategory() != null && p.getCategory().getName() != null) ? p.getCategory().getName() : "", String.CASE_INSENSITIVE_ORDER);
                break;
            case "stock":
                fieldComparator = Comparator.comparingDouble(Product::getStock);
                break;
            case "expirationdate":
                fieldComparator = Comparator.comparing(Product::getExpirationDate, Comparator.nullsLast(LocalDate::compareTo));
                break;
            default:
                return (p1, p2) -> 0;
        }

        return ascending ? fieldComparator : fieldComparator.reversed();
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

        Map<Long, List<Product>> groupedByCategoryId = allProducts.stream()
                .filter(p -> p.getCategory() != null && p.getCategory().getId() != null)
                .collect(Collectors.groupingBy(p -> p.getCategory().getId()));

        List<InventoryMetricsDTO> metrics = new ArrayList<>();

        for (Map.Entry<Long, List<Product>> entry : groupedByCategoryId.entrySet()) {
            Long categoryId = entry.getKey();
            List<Product> productsInCategory = entry.getValue();
            String categoryName = productsInCategory.get(0).getCategory().getName();
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

            InventoryMetricsDTO metric = new InventoryMetricsDTO(categoryId, categoryName, totalStock, totalValue, averagePrice);
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

        InventoryMetricsDTO overallMetrics = new InventoryMetricsDTO(0L,"Overall", overallTotalStock, overallTotalValue,
                overallAveragePrice);

        metrics.add(overallMetrics);

        return metrics;
    }
}
