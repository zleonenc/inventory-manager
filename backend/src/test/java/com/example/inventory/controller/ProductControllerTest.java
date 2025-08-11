package com.example.inventory.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import java.time.LocalDate;
import java.util.List;

import static com.example.inventory.testutil.builders.ProductDTOBuilder.aProductDTO;
import static com.example.inventory.testutil.builders.ProductBuilder.aProduct;
import com.example.inventory.dto.PagedResponse;
import com.example.inventory.dto.ProductDTO;
import com.example.inventory.model.Product;
import com.example.inventory.service.ProductService;
import com.example.inventory.exception.NotFoundException;
import com.example.inventory.testutil.ProductQueryParams;

class ProductControllerTest {

    private ProductService service;
    private ProductController controller;

    @BeforeEach
    void setUp() {
        service = mock(ProductService.class);
        controller = new ProductController(service, null);
    }

    @Test
    void createProduct_returnsSavedProduct() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        ProductDTO dto = aProductDTO().expiringOn(date).build();
        Product saved = aProduct().withId(1L).expiringOn(date).build();

        when(service.saveFromDTO(ArgumentMatchers.any(ProductDTO.class))).thenReturn(saved);

        ResponseEntity<Product> response = controller.createProduct(dto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(saved, response.getBody());
        verify(service).saveFromDTO(dto);
    }

    @Test
    void getFilteredSortedProducts_returnsPagedResponse() {
        Product product1 = aProduct().withId(1L).build();
        Product product2 = aProduct().withId(2L).withName("Product B").build();
        List<Product> products = List.of(product1, product2);
        PagedResponse<Product> paged = new PagedResponse<>(products, 2);

        when(service.getFilteredSortedProducts(
                any(), any(), any(), anyInt(), anyInt(), any(), any(), any(), any())).thenReturn(paged);

        ResponseEntity<PagedResponse<Product>> response = ProductQueryParams.defaults().page(0).size(10)
                .execute(controller);

        assertEquals(200, response.getStatusCode().value());
        PagedResponse<Product> body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.getContent().size());
        verify(service).getFilteredSortedProducts(
                null, null, null, 0, 10, null, "asc", null, "asc");
    }

    @Test
    void updateProductById_found_returnsUpdated() {
        LocalDate date = LocalDate.of(2025, 2, 2);
        ProductDTO dto = aProductDTO().withName("Updated").withPrice(20.0).expiringOn(date).build();
        Product updated = aProduct().withId(1L).withName("Updated").withPrice(20.0).expiringOn(date).build();

        when(service.updateProductById(1L, dto)).thenReturn(updated);

        ResponseEntity<Product> response = controller.updateProduct(1L, dto);

        assertEquals(200, response.getStatusCode().value());
        Product bodyUpdated = response.getBody();
        assertNotNull(bodyUpdated);
        assertEquals(updated, bodyUpdated);
        assertEquals(20.0, bodyUpdated.getPrice());
        verify(service).updateProductById(1L, dto);
    }

    @Test
    void updateProductById_NotFound_throwsNotFoundException() {
        ProductDTO dto = aProductDTO().build();
        when(service.updateProductById(1L, dto)).thenThrow(new NotFoundException("not found"));

        assertThrows(NotFoundException.class, () -> controller.updateProduct(1L, dto));
        verify(service).updateProductById(1L, dto);
    }

    @Test
    void deleteProductById_found_returnsNoContent() {
        ResponseEntity<Void> response = controller.deleteProduct(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(service).deleteProductById(1L);
    }

    @Test
    void deleteProductById_NotFound_throwsNotFoundException() {
        org.mockito.Mockito.doThrow(new NotFoundException("not found"))
                .when(service).deleteProductById(1L);

        assertThrows(NotFoundException.class, () -> controller.deleteProduct(1L));
        verify(service).deleteProductById(1L);
    }

    @Test
    void clearProducts_returnsNoContent() {
        ResponseEntity<Void> response = controller.clearProducts();

        assertEquals(204, response.getStatusCode().value());
        verify(service).clearProducts();
    }
}