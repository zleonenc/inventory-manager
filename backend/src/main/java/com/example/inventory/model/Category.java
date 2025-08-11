package com.example.inventory.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 120, message = "Name must be between 1 and 120 characters")
    private String name;

    @Builder.Default
    private LocalDate creationDate = LocalDate.now();
    @Builder.Default
    private LocalDate updateDate = LocalDate.now();
    @Builder.Default
    private boolean active = true;

}
