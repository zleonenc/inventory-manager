package com.example.inventory.repository;

import org.springframework.stereotype.Repository;

import com.example.inventory.model.Category;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;


@Repository
public class CategoryRepository {
    private final Map<Long, Category> data = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public Category save(Category category){
        if (category.getId() == null){
            category.setId(idGenerator.incrementAndGet());
        }
        data.put(category.getId(), category);
        return category;
    }

    public boolean deleteById(Long id){
        return data.remove(id) != null;
    }

    public Optional<Category> findById(Long id){
        return Optional.ofNullable(data.get(id));
    }

    public List<Category> findAll(){
        return new ArrayList<>(data.values());
    }

    public void clear(){
        data.clear();
    }
}
