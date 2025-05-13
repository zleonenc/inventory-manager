package com.example.inventory.dto;

import java.time.LocalDate;

public class ProductUpdateDTO {
    private String name;
    private Long categoriaId;
    private double price;
    private double stock;;
    private LocalDate expirationDate;
    private boolean active = true;

    public ProductUpdateDTO(String name, Long categoriaId, double price, double stock, LocalDate expirationDate,
            boolean active) {
        this.name = name;
        this.categoriaId = categoriaId;
        this.price = price;
        this.stock = stock;
        this.expirationDate = expirationDate;
        this.active = active;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoriaId() {
        return this.categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
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
