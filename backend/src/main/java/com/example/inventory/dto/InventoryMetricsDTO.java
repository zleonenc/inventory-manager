package com.example.inventory.dto;

public class InventoryMetricsDTO {
    private String categoryName;
    private double totalStock;
    private double totalValue;
    private double averagePrice;

    public InventoryMetricsDTO() {
    }

    public InventoryMetricsDTO(String categoryName, double totalStock, double totalValue, double averagePrice) {
        this.categoryName = categoryName;
        this.totalStock = totalStock;
        this.totalValue = totalValue;
        this.averagePrice = averagePrice;
    }


    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getTotalStock() {
        return this.totalStock;
    }

    public void setTotalStock(double totalStock) {
        this.totalStock = totalStock;
    }

    public double getTotalValue() {
        return this.totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public double getAveragePrice() {
        return this.averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

}
