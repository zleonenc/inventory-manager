package com.example.inventory.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.inventory.dto.ProductDTO;
import com.example.inventory.model.Category;
import com.example.inventory.model.Product;
import com.example.inventory.repository.ProductRepository;

class ProductServiceTest {
    private ProductRepository repository;
    private CategoryService categoryService;
    private ProductFileStorageService storageService;
    private ProductService service;
    private static final double DEFAULT_RESTOCK = 10.0;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepository.class);
        categoryService = mock(CategoryService.class);
        storageService = mock(ProductFileStorageService.class);
        service = new ProductService(repository, categoryService, storageService);
    }

    @Test
    void saveFromDTO_validProduct_savesAndReturnsProduct() {
        Category category = new Category(1L, "Category A");

        ProductDTO dto = new ProductDTO("Product A", 10.0, 10, 1L, LocalDate.now());
        Product saved = new Product("Product A", category, 10.0, 10, dto.getExpirationDate());
        saved.setId(1L);

        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(repository.save(any(Product.class))).thenReturn(saved);

        Product result = service.saveFromDTO(dto);

        assertNotNull(result);
        assertEquals("Product A", result.getName());
        assertEquals(1L, result.getId());
        assertEquals(10.0, result.getPrice());
        assertEquals(10, result.getStock());
        assertEquals(LocalDate.now(), result.getExpirationDate());
        assertTrue(result.isActive());
        verify(repository).save(any(Product.class));
        verify(storageService).saveProducts(any());
    }

    @Test
    void saveFromDTO_invalidCategory_throwsException() {
        ProductDTO dto = new ProductDTO("Product A1", 10.0, 10.0, 1L, LocalDate.now());
        when(categoryService.getCategoryById(1000L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.saveFromDTO(dto));
        assertEquals("Invalid category ID", ex.getMessage());
    }

    @Test
    void getAllProducts_returnsOnlyActive() {
        Category category = new Category(1L, "Category A");
        Product active = new Product(1L, "Product A", category, 10.0, 10, LocalDate.now());
        Product inactive = new Product(2L, "Product B", category, 10.0, 10, LocalDate.now());
        inactive.setActive(false);

        when(repository.getAll()).thenReturn(List.of(active, inactive));

        List<Product> result = service.getAllProducts();
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
        assertEquals("Product A", result.get(0).getName());
    }

    @Test
    void getProductById_activeProduct_returnsProduct() {
        Product product = new Product(1L, "Product A", new Category(1L, "Category A"), 10.0, 10, LocalDate.now());

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> found = service.getProductById(1L);
        assertTrue(found.isPresent());
        assertTrue(found.get().isActive());
        assertEquals(1L, found.get().getId());
        assertEquals("Product A", found.get().getName());
    }

    @Test
    void getProductById_inactiveProduct_returnsEmpty() {
        Product product = new Product(1L, "Product A", new Category(1L, "Category A"), 10.0, 10, LocalDate.now());
        product.setActive(false);

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> found = service.getProductById(1L);
        assertFalse(found.isPresent());
    }

    @Test
    void deleteProductById_foundActive_setsInactiveAndSaves() {
        Product product = new Product("Product A", new Category(1L, "Category A"), 10.0, 10, LocalDate.now());

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.deleteProductById(1L);

        assertFalse(product.isActive());
        verify(repository).save(product);
        verify(storageService).saveProducts(any());
    }

    @Test
    void deleteProductById_notFound_throwsException() {
        when(repository.findById(1000L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.deleteProductById(1000L));
        assertEquals("Product not found with ID: 1000", ex.getMessage());
    }

    @Test
    void deleteProductById_inactive_throwsException() {
        Product product = new Product();
        product.setId(1L);
        product.setActive(false);

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.deleteProductById(1L));
        assertEquals("Product not found with ID: 1", ex.getMessage());
    }

    @Test
    void updateProductById_valid_updatesAndReturnsProduct() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setActive(true);
        existing.setName("Existing Product");

        Category category = new Category();
        category.setId(2L);

        ProductDTO dto = new ProductDTO("Updated", 10.0, 10, 1L, LocalDate.now());

        Product updated = new Product("Updated", category, 10.0, 10, dto.getExpirationDate());
        updated.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(repository.updateById(eq(1L), any(Product.class))).thenReturn(updated);

        Product result = service.updateProductById(1L, dto);

        assertEquals("Updated", result.getName());
        assertEquals(1L, result.getId());
        assertEquals(10.0, result.getPrice());
        assertEquals(10, result.getStock());
        assertEquals(LocalDate.now(), result.getExpirationDate());
        verify(repository).updateById(eq(1L), any(Product.class));
        verify(storageService).saveProducts(any());
    }

    @Test
    void updateProductById_notFound_throwsException() {
        ProductDTO dto = new ProductDTO("Updated", 10.0, 10.0, 1L, LocalDate.now());
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.updateProductById(1L, dto));
        assertEquals("Product not found with ID: 1", ex.getMessage());
    }

    @Test
    void updateProductById_invalidCategory_throwsException() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setActive(true);

        ProductDTO dto = new ProductDTO("Updated", 10.0, 10.0, 1L, LocalDate.now());

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryService.getCategoryById(2L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.updateProductById(1L, dto));
        assertEquals("Invalid category ID", ex.getMessage());
    }

    @Test
    void markProductAsOutOfStock_setsStockToZero_andSaves() {
        Product product = new Product();
        product.setId(1L);
        product.setActive(true);
        product.setStock(10);

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.markProductAsOutOfStock(1L);

        assertEquals(0, product.getStock());
        verify(repository).save(product);
        verify(storageService).saveProducts(any());
    }

    @Test
    void markProductAsOutOfStock_notFound_throwsException() {
        when(repository.findById(1000L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.markProductAsOutOfStock(1000L));
        assertEquals("Product not found with ID: 1000", ex.getMessage());
    }

    @Test
    void markProductAsInStock_setsStockToOne_andSaves() {
        Product product = new Product();
        product.setId(1L);
        product.setActive(true);
        product.setStock(0);

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.markProductAsInStock(1L);

        assertEquals(DEFAULT_RESTOCK, product.getStock());
        verify(repository).save(product);
        verify(storageService).saveProducts(any());
    }

    @Test
    void markProductAsInStock_notFound_throwsException() {
        when(repository.findById(1000L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.markProductAsInStock(1000L));
        assertEquals("Product not found with ID: 1000", ex.getMessage());
    }
}
