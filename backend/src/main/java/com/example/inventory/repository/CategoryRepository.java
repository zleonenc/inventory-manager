package com.example.inventory.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import com.example.inventory.model.Category;

@Repository
public class CategoryRepository {
    private final Map<Long, Category> data = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public CategoryRepository() {
    }

    public Category save(Category category) {
        category.setId(idGenerator.incrementAndGet());
        data.put(category.getId(), category);
        return category;
    }

    public Category updateById(Long id, Category category) {
        category.setId(id);
        data.put(id, category);
        return category;
    }

    public boolean deleteById(Long id) {
        return data.remove(id) != null;
    }

    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    public List<Category> getAll() {
        return new ArrayList<>(data.values());
    }

    public void clear() {
        data.clear();
    }

    public void loadCategories(List<Category> categories) {
        for (Category category : categories) {
            save(category);
        }

        Long maxId = data.keySet().stream().max(Long::compareTo).orElse(0L);
        idGenerator.set(maxId);
    }

    public AtomicLong getIdGenerator() {
        return idGenerator;
    }
}
