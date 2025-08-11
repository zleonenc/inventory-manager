package com.example.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMetricsDTO {
    private Long categoryId;
    private String categoryName;
    private double totalStock;
    private double totalValue;
    private double averagePrice;
}
