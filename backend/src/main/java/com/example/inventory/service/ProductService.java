package com.example.inventory.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.inventory.config.ErrorMessages.*;
import com.example.inventory.exception.NotFoundException;
import com.example.inventory.repository.ProductRepository;
import com.example.inventory.dto.ProductDTO;
import com.example.inventory.dto.PagedResponse;
import com.example.inventory.model.Category;
import com.example.inventory.model.Product;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    private static final int DEFAULT_RESTOCK = 10;
    private static final String AVAIL_IN_STOCK = "instock";
    private static final String AVAIL_OUT_OF_STOCK = "outofstock";

    private static final String SORT_NAME = "name";
    private static final String SORT_PRICE = "price";
    private static final String SORT_CATEGORY = "category";
    private static final String SORT_STOCK = "stock";
    private static final String SORT_EXPIRATION_DATE = "expirationdate";

    private static final Map<String, Comparator<Product>> SORT_REGISTRY = Map.of(
            SORT_NAME,
            Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER),
            SORT_PRICE,
            Comparator.comparingDouble(Product::getPrice),
            SORT_CATEGORY,
            Comparator.comparing(
                    p -> (p.getCategory() != null && p.getCategory().getName() != null) ? p.getCategory().getName()
                            : "",
                    String.CASE_INSENSITIVE_ORDER),
            SORT_STOCK,
            Comparator.comparingDouble(Product::getStock),
            SORT_EXPIRATION_DATE,
            Comparator.comparing(Product::getExpirationDate, Comparator.nullsLast(LocalDate::compareTo)));

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public Product saveFromDTO(ProductDTO productDTO) {
        Category category = categoryService.getCategoryById(productDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(INVALID_CATEGORY_ID));
    Product product = Product.builder()
        .name(productDTO.getName())
        .category(category)
        .price(productDTO.getPrice())
        .stock(productDTO.getStock())
        .expirationDate(productDTO.getExpirationDate())
        .build();

        Product savedProduct = productRepository.save(product);
        return savedProduct;
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.getAll().stream()
                .collect(Collectors.toList());
    }

    public PagedResponse<Product> getFilteredSortedProducts(
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
                                (AVAIL_IN_STOCK.equalsIgnoreCase(available) && p.getStock() > 0) ||
                                (AVAIL_OUT_OF_STOCK.equalsIgnoreCase(available) && p.getStock() == 0)))
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
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());

        return new PagedResponse<>(paged, totalElements);
    }

    private Comparator<Product> createComparator(String sortByField, String direction) {
        if (sortByField == null || sortByField.isEmpty()) {
            return (p1, p2) -> 0;
        }

        boolean ascending = (direction == null || "asc".equalsIgnoreCase(direction));
        Comparator<Product> fieldComparator = SORT_REGISTRY.get(sortByField.toLowerCase());
        if (fieldComparator == null) {
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
            throw new NotFoundException(String.format(PRODUCT_NOT_FOUND, id));
        }

        Product product = productOptional.get();

        product.setActive(false); // Soft delete
        product.setUpdateDate(LocalDate.now());

        productRepository.updateById(id, product);
    }

    public Product updateProductById(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(PRODUCT_NOT_FOUND, id)));
        if (!existingProduct.isActive()) {
            throw new NotFoundException(String.format(PRODUCT_NOT_FOUND, id));
        }

        Category category = categoryService.getCategoryById(productDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(INVALID_CATEGORY_ID));

        existingProduct.setName(productDTO.getName());
        existingProduct.setCategory(category);
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setStock(productDTO.getStock());
        existingProduct.setExpirationDate(productDTO.getExpirationDate());
        existingProduct.setUpdateDate(LocalDate.now());

        Product updatedProduct = productRepository.updateById(id, existingProduct);

        return updatedProduct;
    }

    public Product markProductAsOutOfStock(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(PRODUCT_NOT_FOUND, id)));
        if (!product.isActive()) {
            throw new NotFoundException(String.format(PRODUCT_NOT_FOUND, id));
        }

        product.setStock(0);
        product.setUpdateDate(LocalDate.now());

        Product updatedProduct = productRepository.updateById(id, product);
        return updatedProduct;
    }

    public Product markProductAsInStock(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(PRODUCT_NOT_FOUND, id)));
        if (!product.isActive()) {
            throw new NotFoundException(String.format(PRODUCT_NOT_FOUND, id));
        }

        product.setStock(DEFAULT_RESTOCK);
        product.setUpdateDate(LocalDate.now());

        Product updatedProduct = productRepository.updateById(id, product);
        return updatedProduct;
    }

    public void clearProducts() {
        productRepository.clear();
    }
}
