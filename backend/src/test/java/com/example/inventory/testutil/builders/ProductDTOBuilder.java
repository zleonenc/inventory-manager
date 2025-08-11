package com.example.inventory.testutil.builders;

import java.time.LocalDate;

import com.example.inventory.dto.ProductDTO;

public class ProductDTOBuilder {
    private String name = "Product A";
    private double price = 10.0;
    private double stock = 10.0;
    private Long categoryId = 1L;
    private LocalDate expirationDate = null;
    private boolean active = true;

    public static ProductDTOBuilder aProductDTO() {
        return new ProductDTOBuilder();
    }

    public ProductDTOBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductDTOBuilder withPrice(double price) {
        this.price = price;
        return this;
    }

    public ProductDTOBuilder withStock(double stock) {
        this.stock = stock;
        return this;
    }

    public ProductDTOBuilder withCategoryId(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public ProductDTOBuilder expiringOn(LocalDate date) {
        this.expirationDate = date;
        return this;
    }

    public ProductDTOBuilder inactive() {
        this.active = false;
        return this;
    }

    public ProductDTO build() {
        ProductDTO dto = new ProductDTO(this.name, this.price, this.stock, this.categoryId, this.expirationDate);
        dto.setActive(this.active);
        return dto;
    }
}
