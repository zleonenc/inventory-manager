package com.example.inventory.config;

public final class ErrorMessages {
    private ErrorMessages() {
    }

    public static final String CATEGORY_NOT_FOUND = "Category not found with ID: %s";
    public static final String PRODUCT_NOT_FOUND = "Product not found with ID: %s";
    public static final String DUPLICATE_CATEGORY_NAME = "Category with the same name already exists";
    public static final String INVALID_CATEGORY_ID = "Invalid category ID";
}
