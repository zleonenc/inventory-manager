package com.example.inventory.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class Category {
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 120, message = "Name must be between 1 and 120 characters")
    private String name;

    private LocalDate creationDate = LocalDate.now();
    private LocalDate updateDate = LocalDate.now();
    private boolean active = true;

    public Category() {
    }

    public Category(String name, boolean active) {
        this.name = name;
        this.active = active;
    }

    public Category(String name) {
        this.name = name;
    }

    public Category(Long id, String name, boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
    }

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
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

    public boolean getActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
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

}
