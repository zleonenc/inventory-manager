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

import com.example.inventory.exception.NotFoundException;
import static com.example.inventory.testutil.builders.ProductBuilder.aProduct;
import static com.example.inventory.testutil.builders.ProductDTOBuilder.aProductDTO;
import static com.example.inventory.testutil.builders.CategoryBuilder.aCategory;
import com.example.inventory.repository.ProductRepository;
import com.example.inventory.model.Category;
import com.example.inventory.model.Product;
import com.example.inventory.dto.ProductDTO;

class ProductServiceTest {
    private ProductRepository repository;
    private CategoryService categoryService;
    private ProductService service;
    private static final double DEFAULT_RESTOCK = 10.0;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepository.class);
        categoryService = mock(CategoryService.class);
        service = new ProductService(repository, categoryService);
    }

    @Test
    void saveFromDTO_validProduct_savesAndReturnsProduct() {
        Category category = aCategory().build();
        LocalDate date = LocalDate.of(2025, 1, 1);
        ProductDTO dto = aProductDTO().expiringOn(date).build();
        Product saved = aProduct().withId(1L).expiringOn(date).build();

        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(repository.save(any(Product.class))).thenReturn(saved);

        Product result = service.saveFromDTO(dto);

        assertNotNull(result);
        assertEquals("Product A", result.getName());
        assertEquals(1L, result.getId());
        assertEquals(10.0, result.getPrice());
        assertEquals(10, result.getStock());
        assertEquals(date, result.getExpirationDate());
        assertTrue(result.isActive());
        verify(repository).save(any(Product.class));
    }

    @Test
    void saveFromDTO_invalidCategory_throwsException() {
        ProductDTO dto = aProductDTO().build();
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.saveFromDTO(dto));
        assertEquals("Invalid category ID", ex.getMessage());
    }

    @Test
    void getFilteredSortedProducts_returnsOnlyActive() {
        Product active = aProduct().withId(1L).build();
        Product inactive = aProduct().withId(2L).inactive().build();

        when(repository.getAll()).thenReturn(List.of(active, inactive));

        List<Product> result = com.example.inventory.testutil.ProductQueryParams
                .defaults()
                .page(0)
                .size(10)
                .execute(service)
                .getContent();
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
        assertEquals("Product A", result.get(0).getName());
    }

    @Test
    void getProductById_activeProduct_returnsProduct() {
        Product product = aProduct().withId(1L).build();

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> found = service.getProductById(1L);
        assertTrue(found.isPresent());
        assertTrue(found.get().isActive());
        assertEquals(1L, found.get().getId());
        assertEquals("Product A", found.get().getName());
    }

    @Test
    void getProductById_inactiveProduct_returnsEmpty() {
        Product product = aProduct().withId(1L).inactive().build();

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> found = service.getProductById(1L);
        assertFalse(found.isPresent());
    }

    @Test
    void deleteProductById_foundActive_setsInactiveAndSaves() {
        Product product = aProduct().withId(1L).build();

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.deleteProductById(1L);

        assertFalse(product.isActive());
        verify(repository).updateById(eq(1L), eq(product));
    }

    @Test
    void deleteProductById_notFound_throwsException() {
        when(repository.findById(1000L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(NotFoundException.class, () -> service.deleteProductById(1000L));
        assertEquals("Product not found with ID: 1000", ex.getMessage());
    }

    @Test
    void deleteProductById_inactive_throwsException() {
        Product product = aProduct().withId(1L).inactive().build();

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        Exception ex = assertThrows(NotFoundException.class, () -> service.deleteProductById(1L));
        assertEquals("Product not found with ID: 1", ex.getMessage());
    }

    @Test
    void updateProductById_valid_updatesAndReturnsProduct() {
        Product existing = aProduct().withId(1L).build();
        Category category = aCategory().withId(2L).build();
        LocalDate date = LocalDate.of(2025, 2, 2);
        ProductDTO dto = aProductDTO().withName("Updated").expiringOn(date).build();
        Product updated = aProduct().withId(1L).withName("Updated").expiringOn(date).build();

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(repository.updateById(eq(1L), any(Product.class))).thenReturn(updated);

        Product result = service.updateProductById(1L, dto);

        assertEquals("Updated", result.getName());
        assertEquals(1L, result.getId());
        assertEquals(10.0, result.getPrice());
        assertEquals(10, result.getStock());
        assertEquals(date, result.getExpirationDate());
        verify(repository).updateById(eq(1L), any(Product.class));
    }

    @Test
    void updateProductById_notFound_throwsException() {
        ProductDTO dto = aProductDTO().build();
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(NotFoundException.class, () -> service.updateProductById(1L, dto));
        assertEquals("Product not found with ID: 1", ex.getMessage());
    }

    @Test
    void updateProductById_invalidCategory_throwsException() {
        Product existing = aProduct().withId(1L).build();
        ProductDTO dto = aProductDTO().build();

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.updateProductById(1L, dto));
        assertEquals("Invalid category ID", ex.getMessage());
    }

    @Test
    void markProductAsOutOfStock_setsStockToZeroAndSaves() {
        Product product = aProduct().withId(1L).build();

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.markProductAsOutOfStock(1L);

        assertEquals(0, product.getStock());
        repository.updateById(eq(1L), eq(product));
    }

    @Test
    void markProductAsOutOfStock_notFound_throwsException() {
        when(repository.findById(1000L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(NotFoundException.class, () -> service.markProductAsOutOfStock(1000L));
        assertEquals("Product not found with ID: 1000", ex.getMessage());
    }

    @Test
    void markProductAsInStock_setsStockToOneAndSaves() {
        Product product = aProduct().withId(1L).withStock(0).build();

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.markProductAsInStock(1L);

        assertEquals(DEFAULT_RESTOCK, product.getStock());
        repository.updateById(eq(1L), eq(product));
    }

    @Test
    void markProductAsInStock_notFound_throwsException() {
        when(repository.findById(1000L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(NotFoundException.class, () -> service.markProductAsInStock(1000L));
        assertEquals("Product not found with ID: 1000", ex.getMessage());
    }
}
