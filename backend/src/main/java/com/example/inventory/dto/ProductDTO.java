package com.example.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 120, message = "Name must be between 1 and 120 characters")
    private String name;

    @PositiveOrZero(message = "Price must be positive or zero")
    private double price;

    @PositiveOrZero(message = "Stock must be positive or zero")
    private double stock;

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive")
    private Long categoryId;

    private LocalDate expirationDate;

    @Builder.Default
    private boolean active = true;

    // Preserve existing 5-arg convenience constructor used in code/tests
    public ProductDTO(String name, double price, double stock, Long categoryId, LocalDate expirationDate) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.expirationDate = expirationDate;
    }
}
