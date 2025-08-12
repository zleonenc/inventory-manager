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

    /** Registry of sort comparators for different fields. */
    private static final Map<String, Comparator<Product>> SORT_REGISTRY = Map.of(
            SORT_NAME,
            Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER),
            SORT_PRICE,
            Comparator.comparingDouble(Product::getPrice),
            SORT_CATEGORY,
            Comparator.comparing(
                    p -> (p.getCategory() != null && p.getCategory().getName() != null) 
                        ? p.getCategory().getName() : "",
                    String.CASE_INSENSITIVE_ORDER),
            SORT_STOCK,
            Comparator.comparingDouble(Product::getStock),
            SORT_EXPIRATION_DATE,
            Comparator.comparing(Product::getExpirationDate, 
                Comparator.nullsLast(LocalDate::compareTo)));
                
    public ProductService(final ProductRepository repository, 
            final CategoryService service) {
        this.productRepository = repository;
        this.categoryService = service;
    }

    /**
     * Saves a new product from a ProductDTO.
     *
     * @param productDTO the product data transfer object
     * @return the saved product
     * @throws IllegalArgumentException if the category ID is invalid
     */
    public Product saveFromDTO(final ProductDTO productDTO) {
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

    /**
     * Saves a product to the repository.
     *
     * @param product the product to save
     * @return the saved product
     */
    public Product saveProduct(final Product product) {
        return productRepository.save(product);
    }

    /**
     * Retrieves all products from the repository.
     *
     * @return list of all products
     */
    public List<Product> getAllProducts() {
        return productRepository.getAll().stream()
                .collect(Collectors.toList());
    }

    /**
     * Retrieves filtered, sorted, and paginated products.
     *
     * @param name the product name filter (optional)
     * @param categories the list of category IDs to filter by (optional)
     * @param available the availability filter (optional)
     * @param page the page number (0-based)
     * @param size the page size
     * @param primarySortBy the primary sort field (optional)
     * @param primarySortDirection the primary sort direction
     * @param secondarySortBy the secondary sort field (optional)
     * @param secondarySortDirection the secondary sort direction
     * @return the paginated list of filtered products
     */
    public PagedResponse<Product> getFilteredSortedProducts(
            final String name, final List<Long> categories, final String available,
            final int page, final int size, final String primarySortBy, 
            final String primarySortDirection, final String secondarySortBy, 
            final String secondarySortDirection) {

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

    /**
     * Creates a comparator for sorting products by the specified field and direction.
     *
     * @param sortByField the field to sort by
     * @param direction the sort direction (asc/desc)
     * @return the comparator for the specified field and direction
     */
    private Comparator<Product> createComparator(final String sortByField, 
            final String direction) {
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

    /**
     * Retrieves an active product by its ID.
     *
     * @param id the ID of the product to retrieve
     * @return optional containing the product if found and active, empty otherwise
     */
    public Optional<Product> getProductById(final Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent() && product.get().isActive()) {
            return product;
        } else {
            return Optional.empty();
        }
    }

    /**
     * Retrieves all active products by category ID.
     *
     * @param categoryId the ID of the category to filter by
     * @return list of active products in the specified category
     */
    public List<Product> getProductsByCategory(final Long categoryId) {
        List<Product> products = productRepository.getAll();
        return products.stream()
                .filter(Product::isActive)
                .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());

    }

    /**
     * Deletes a product by marking it as inactive (soft delete).
     *
     * @param id the ID of the product to delete
     * @throws NotFoundException if the product is not found or already inactive
     */
    public void deleteProductById(final Long id) {
        Optional<Product> productOptional = productRepository.findById(id);

        if (productOptional.isEmpty() || !productOptional.get().isActive()) {
            throw new NotFoundException(String.format(PRODUCT_NOT_FOUND, id));
        }

        Product product = productOptional.get();

        product.setActive(false); // Soft delete
        product.setUpdateDate(LocalDate.now());

        productRepository.updateById(id, product);
    }

    /**
     * Updates an existing product by ID.
     *
     * @param id the ID of the product to update
     * @param productDTO the updated product data transfer object
     * @return the updated product
     * @throws NotFoundException if the product is not found or inactive
     * @throws IllegalArgumentException if the category ID is invalid
     */
    public Product updateProductById(final Long id, final ProductDTO productDTO) {
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

    /**
     * Marks a product as out of stock by setting its stock to 0.
     *
     * @param id the ID of the product to mark as out of stock
     * @return the updated product
     * @throws NotFoundException if the product is not found or inactive
     */
    public Product markProductAsOutOfStock(final Long id) {
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

    /**
     * Marks a product as in stock by setting its stock to the default restock amount.
     *
     * @param id the ID of the product to mark as in stock
     * @return the updated product
     * @throws NotFoundException if the product is not found or inactive
     */
    public Product markProductAsInStock(final Long id) {
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

    /**
     * Clears all products from the repository.
     */
    public void clearProducts() {
        productRepository.clear();
    }
}
