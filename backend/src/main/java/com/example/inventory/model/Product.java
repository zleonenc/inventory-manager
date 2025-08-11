package com.example.inventory.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 120, message = "Name must be between 1 and 120 characters")
    private String name;

    @NotNull(message = "Category cannot be null")
    private Category category;

    @PositiveOrZero(message = "Price must be positive or zero")
    private double price;

    @PositiveOrZero(message = "Stock must be positive or zero")
    private double stock;

    @Builder.Default
    private LocalDate creationDate = LocalDate.now();
    @Builder.Default
    private LocalDate updateDate = LocalDate.now();
    private LocalDate expirationDate;
    @Builder.Default
    private boolean active = true;
}
