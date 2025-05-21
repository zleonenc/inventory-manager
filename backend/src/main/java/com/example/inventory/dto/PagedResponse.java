package com.example.inventory.dto;

import java.util.List;

public class PagedResponse<T> {
    private List<T> content;
    private long totalElements;

    public PagedResponse(List<T> content, long totalElements) {
        this.content = content;
        this.totalElements = totalElements;
    }

    public List<T> getContent() {
        return content;
    }

    public long getTotalElements() {
        return totalElements;
    }
}