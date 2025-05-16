package com.example.inventory.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.FutureOrPresent;

public class ProductDTO {
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 120, message = "Name must be between 1 and 120 characters")
    private String name;

    @PositiveOrZero(message = "Price must be positive or zero")
    private double price;

    @PositiveOrZero(message = "Stock must be positive or zero")
    private double stock;

    @PositiveOrZero(message = "Category ID must be positive or zero")
    private Long categoryId;

    @FutureOrPresent(message = "Expiration date must be in the present or future")
    private LocalDate expirationDate;

    private boolean active = true;

    public ProductDTO() {
    }

    public ProductDTO(String name, double price, double stock, Long categoryId, LocalDate expirationDate) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.expirationDate = expirationDate;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getStock() {
        return this.stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public LocalDate getExpirationDate() {
        return this.expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean getActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
