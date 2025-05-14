package com.example.inventory.model;

import java.time.LocalDate;

public class Product {
    private Long id;
    private String name;
    private Category category;
    private double price;
    private double stock;
    private LocalDate creationDate = LocalDate.now();
    private LocalDate updateDate = LocalDate.now();
    private LocalDate expirationDate;
    private boolean active = true;

    public Product() {
    }

    public Product(Long id, String name, Category category, double price, double stock, LocalDate expirationDate) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.expirationDate = expirationDate;
    }

    public Product(Long id, String name, Category category, double price, double stock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    public Product(String name, Category category, double price, double stock, LocalDate expirationDate) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.stock = stock;
        this.expirationDate = expirationDate;
    }

    public Product(String name, Category category, double price, double stock) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
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

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

}
