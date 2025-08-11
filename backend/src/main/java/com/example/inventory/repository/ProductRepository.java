package com.example.inventory.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import com.example.inventory.model.Product;

@Repository
public class ProductRepository {
    private final Map<Long, Product> data = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public Product save(Product product) {
        product.setId(idGenerator.incrementAndGet());
        // Ensure reasonable defaults on create
        if (product.getCreationDate() == null) {
            product.setCreationDate(java.time.LocalDate.now());
        }
        if (product.getUpdateDate() == null) {
            product.setUpdateDate(product.getCreationDate());
        }
        // Default to active if not explicitly set
        if (!product.isActive()) {
            product.setActive(true);
        }
        data.put(product.getId(), product);
        return product;
    }

    public Product updateById(Long id, Product product) {
        product.setId(id);
        data.put(id, product);
        return product;
    }

    public boolean deleteById(Long id) {
        return data.remove(id) != null;
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    public List<Product> getAll() {
        return new ArrayList<>(data.values());
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

    public AtomicLong getIdGenerator() {
        return idGenerator;
    }
}
