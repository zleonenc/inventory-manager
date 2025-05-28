package com.example.inventory.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.inventory.dto.PagedResponse;
import com.example.inventory.dto.ProductDTO;
import com.example.inventory.model.Category;
import com.example.inventory.model.Product;
import com.example.inventory.service.ProductService;

class ProductControllerTest {

    private ProductService service;
    private ProductController controller;

    @BeforeEach
    void setUp() {
        service = mock(ProductService.class);
        controller = new ProductController(service);
    }

    @Test
    void createProduct_returnsSavedProduct() {
        ProductDTO dto = new ProductDTO("Product A", 10.0, 10, 1L, LocalDate.now());
        Product saved = new Product("Product A", new Category(1L, "Category A"), 10.0, 10, LocalDate.now());
        saved.setId(1L);

        when(service.saveFromDTO(ArgumentMatchers.any(ProductDTO.class))).thenReturn(saved);

        ResponseEntity<Product> response = controller.createProduct(dto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(saved, response.getBody());
        verify(service).saveFromDTO(dto);
    }

    @Test
    void getFilteredProducts_returnsPagedResponse() {
        Product product1 = new Product(1L, "Product A", new Category(1L, "Category A"), 1.0, 1, LocalDate.now());
        Product product2 = new Product(2L, "Product B", new Category(2L, "Category B"), 2.0, 2, LocalDate.now());
        List<Product> products = List.of(product1, product2);
        PagedResponse<Product> paged = new PagedResponse<>(products, 2);

        when(service.getFilteredProducts(
                any(), any(), any(), anyInt(), anyInt(), any(), any(), any(), any())).thenReturn(paged);

        ResponseEntity<PagedResponse<Product>> response = controller.getFilteredProducts(
                null, // name
                null, // categories
                null, // available
                0, // page
                10, // size
                null, // primarySortBy
                null, // primarySortDirection
                null, // secondarySortBy
                null // secondarySortDirection
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getContent().size());
        verify(service).getFilteredProducts(
                null, null, null, 0, 10, null, null, null, null);
    }

    @Test
    void updateProductById_found_returnsUpdated() {
        ProductDTO dto = new ProductDTO("Updated", 20.0, 10.0, 1L, LocalDate.now());
        Product updated = new Product(1L, "Updated", new Category(1L, "Category A"), 20.0, 10, LocalDate.now());

        when(service.getProductById(1L)).thenReturn(Optional.of(updated));
        when(service.updateProductById(1L, dto)).thenReturn(updated);

        ResponseEntity<Product> response = controller.updateProduct(1L, dto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(updated, response.getBody());
        assertEquals(20.0, response.getBody().getPrice());
        verify(service).getProductById(1L);
        verify(service).updateProductById(1L, dto);
    }

    @Test
    void updateProductById_notFound_returnsNotFound() {
        ProductDTO dto = new ProductDTO("Updated", 20.0, 10.0, 1L, LocalDate.now());

        when(service.getProductById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Product> response = controller.updateProduct(1L, dto);

        assertEquals(404, response.getStatusCode().value());
        verify(service).getProductById(1L);
        verify(service, never()).updateProductById(anyLong(), any(ProductDTO.class));
    }

    @Test
    void deleteProductById_found_returnsNoContent() {
        Product product = new Product(1L, "Product A", new Category(1L, "Category A"), 10.0, 10, LocalDate.now());

        when(service.getProductById(1L)).thenReturn(Optional.of(product));

        ResponseEntity<Void> response = controller.deleteProduct(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(service).getProductById(1L);
        verify(service).deleteProductById(1L);
    }

    @Test
    void deleteProductById_notFound_returnsNotFound() {
        when(service.getProductById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = controller.deleteProduct(1L);

        assertEquals(404, response.getStatusCode().value());
        verify(service).getProductById(1L);
        verify(service, never()).deleteProductById(anyLong());
    }

    @Test
    void clearProducts_returnsNoContent() {
        ResponseEntity<Void> response = controller.clearProducts();

        assertEquals(204, response.getStatusCode().value());
        verify(service).clearProducts();
    }
}