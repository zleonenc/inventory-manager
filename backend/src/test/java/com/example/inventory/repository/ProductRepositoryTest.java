package com.example.inventory.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import com.example.inventory.model.Product;

class ProductRepositoryTest {
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ProductRepository();
    }

    @Test
    void save_Product_returnsSavedProduct() {
        Product product = new Product();
        product.setName("Product A");
        product.setPrice(1000.00);

        Product saved = repository.save(product);
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(1L, saved.getId());
        assertEquals("Product A", saved.getName());
        assertEquals(1000.00, saved.getPrice());
    }

    @Test
    void updateById_ExistingId_returnsUpdatedProduct() {
        Product product = new Product();
        product.setName("Product A");
        product.setPrice(1000.00);

        Product saved = repository.save(product);
        saved.setName("Product B");
        saved.setPrice(1200.00);

        Product updated = repository.updateById(saved.getId(), saved);

        assertNotNull(updated);
        assertEquals("Product B", updated.getName());
        assertEquals(1200.00, updated.getPrice());
    }

    @Test
    void findById_FoundId_returnsProduct() {
        Product product = new Product();
        product.setName("Product A");
        product.setPrice(1000.00);

        Product saved = repository.save(product);
        Optional<Product> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Product A", found.get().getName());
        assertEquals(1000.00, found.get().getPrice());
    }

    @Test
    void findById_NotFoundId_returnsEmpty() {
        Optional<Product> found = repository.findById(1000L);
        assertFalse(found.isPresent());
    }

    @Test
    void deleteById_FoundId_returnsTrue() {
        Product product = new Product();
        Product saved = repository.save(product);

        boolean deleted = repository.deleteById(saved.getId());
        assertTrue(deleted);
        assertFalse(repository.findById(saved.getId()).isPresent());
    }

    @Test
    void deleteById_NotFound_returnsFalse() {
        boolean deleted = repository.deleteById(1000L);
        assertFalse(deleted);
    }

    @Test
    void getAll_ReturnsAllProducts() {
        Product product1 = new Product();
        product1.setName("Product A");
        Product product2 = new Product();
        product2.setName("Product B");

        repository.save(product1);
        repository.save(product2);

        List<Product> allProducts = repository.getAll();
        assertEquals(2, allProducts.size());
        assertTrue(allProducts.contains(product1));
        assertTrue(allProducts.contains(product2));
        assertEquals("Product A", allProducts.get(0).getName());
        assertEquals("Product B", allProducts.get(1).getName());
    }

    @Test
    void clear_removesAllProducts() {
        Product product1 = new Product();
        Product product2 = new Product();
        repository.save(product1);
        repository.save(product2);

        repository.clear();
        List<Product> allProducts = repository.getAll();
        assertTrue(allProducts.isEmpty());
    }

    @Test
    void loadProducts_loadsProductsAndUpdatesIdGenerator() {
        Product product1 = new Product();
        product1.setName("Product A");
        Product product2 = new Product();
        product2.setName("Product B");

        repository.loadProducts(List.of(product1, product2));

        List<Product> allProducts = repository.getAll();
        assertEquals(2, allProducts.size());
        assertTrue(allProducts.contains(product1));
        assertTrue(allProducts.contains(product2));
        assertEquals("Product A", allProducts.get(0).getName());
        assertEquals("Product B", allProducts.get(1).getName());

        assertEquals(2L, repository.getIdGenerator().get());
    }
}