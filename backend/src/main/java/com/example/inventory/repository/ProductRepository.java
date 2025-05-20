package com.example.inventory.repository;

import org.springframework.stereotype.Repository;

import com.example.inventory.model.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {
    private final Map<Long, Product> data = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idGenerator.incrementAndGet());
        }
        data.put(product.getId(), product);
        return product;
    }

    // 
    public boolean deleteById(Long id) {
        Product product = data.get(id);
        if (product != null) {
            product.setActive(false); // Mark the product as inactive
            return true;
        }
        return false;
    }

    public Optional<Product> findById(Long id) {
        Product product = data.get(id);
        if (product != null) {
            return Optional.of(product);
        }
        return Optional.empty();
    }

    public List<Product> getAll() {
        return data.values().stream().collect(Collectors.toList());
    }

    public void clear() {
        data.clear();
    }

    public void loadProducts(List<Product> products) {
        for (Product product : products) {
            save(product);
        }

        Long maxId = data.keySet().stream().max(Long::compareTo).orElse(0L);
        idGenerator.set(maxId);
    }
}
