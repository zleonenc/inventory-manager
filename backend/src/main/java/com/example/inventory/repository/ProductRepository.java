package com.example.inventory.repository;

import org.springframework.stereotype.Repository;

import com.example.inventory.model.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {
    private final Map<Long, Product> data = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idGenerator.incrementAndGet());
        }
        data.put(product.getId(), product);
        return product;
    }

    public boolean deleteById(Long id) {
        return data.remove(id) != null;
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    public List<Product> findAll() {
        return new ArrayList<>(data.values());
    }

    public List<Product> findByCategoryId(Long categoryId) {
        return data.values().stream().filter(p -> p.getCategory() != null && p.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
    }

    public void clear() {
        data.clear();
    }
}
