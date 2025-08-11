package com.example.inventory.testutil;

import java.util.List;

import com.example.inventory.controller.ProductController;
import com.example.inventory.dto.PagedResponse;
import com.example.inventory.model.Product;
import com.example.inventory.service.ProductService;
import org.springframework.http.ResponseEntity;

public class ProductQueryParams {
    private String name;
    private List<Long> categories;
    private String available;
    private int page = 0;
    private int size = 10;
    private String primarySortBy;
    private String primarySortDirection = "asc";
    private String secondarySortBy;
    private String secondarySortDirection = "asc";

    public static ProductQueryParams defaults() {
        return new ProductQueryParams();
    }

    public ProductQueryParams name(String name) {
        this.name = name;
        return this;
    }

    public ProductQueryParams categories(List<Long> categories) {
        this.categories = categories;
        return this;
    }

    public ProductQueryParams available(String available) {
        this.available = available;
        return this;
    }

    public ProductQueryParams page(int page) {
        this.page = page;
        return this;
    }

    public ProductQueryParams size(int size) {
        this.size = size;
        return this;
    }

    public ProductQueryParams primarySortBy(String s) {
        this.primarySortBy = s;
        return this;
    }

    public ProductQueryParams primarySortDirection(String d) {
        this.primarySortDirection = d;
        return this;
    }

    public ProductQueryParams secondarySortBy(String s) {
        this.secondarySortBy = s;
        return this;
    }

    public ProductQueryParams secondarySortDirection(String d) {
        this.secondarySortDirection = d;
        return this;
    }

    public ResponseEntity<PagedResponse<Product>> execute(ProductController controller) {
        return controller.getFilteredSortedProducts(
                name, categories, available,
                page, size,
                primarySortBy, primarySortDirection,
                secondarySortBy, secondarySortDirection);
    }

    /**
     * Execute directly against the service layer. Useful for service tests to avoid
     * long parameter lists.
     */
    public PagedResponse<Product> execute(ProductService service) {
        return service.getFilteredSortedProducts(
                name, categories, available,
                page, size,
                primarySortBy, primarySortDirection,
                secondarySortBy, secondarySortDirection);
    }
}
