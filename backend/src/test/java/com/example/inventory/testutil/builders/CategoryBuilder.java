package com.example.inventory.testutil.builders;

import com.example.inventory.model.Category;

public class CategoryBuilder {
    private Long id = 1L;
    private String name = "Category A";
    private boolean active = true;

    public static CategoryBuilder aCategory() {
        return new CategoryBuilder();
    }

    public CategoryBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public CategoryBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CategoryBuilder inactive() {
        this.active = false;
        return this;
    }

    public Category build() {
        return Category.builder()
                .id(this.id)
                .name(this.name)
                .active(this.active)
                .build();
    }
}
