package com.example.inventory.testutil.builders;

import java.time.LocalDate;

import com.example.inventory.model.Category;
import com.example.inventory.model.Product;

public class ProductBuilder {
    private Long id;
    private String name = "Product A";
    private Category category = Category.builder().id(1L).name("Category A").build();
    private double price = 10.0;
    private double stock = 10.0;
    private LocalDate expirationDate = null;
    private boolean active = true;

    public static ProductBuilder aProduct() {
        return new ProductBuilder();
    }

    public ProductBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ProductBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder withCategory(Category category) {
        this.category = category;
        return this;
    }

    public ProductBuilder withPrice(double price) {
        this.price = price;
        return this;
    }

    public ProductBuilder withStock(double stock) {
        this.stock = stock;
        return this;
    }

    public ProductBuilder expiringOn(LocalDate date) {
        this.expirationDate = date;
        return this;
    }

    public ProductBuilder inactive() {
        this.active = false;
        return this;
    }

    public Product build() {
        return Product.builder()
                .id(this.id)
                .name(this.name)
                .category(this.category)
                .price(this.price)
                .stock(this.stock)
                .expirationDate(this.expirationDate)
                .active(this.active)
                .build();
    }
}
