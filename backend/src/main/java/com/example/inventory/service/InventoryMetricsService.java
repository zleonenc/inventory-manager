package com.example.inventory.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.inventory.dto.InventoryMetricsDTO;
import com.example.inventory.model.Product;

@Service
public class InventoryMetricsService {
    private final ProductService productService;

    public InventoryMetricsService(final ProductService service) {
        this.productService = service;
    }

    /**
     * Calculates and retrieves inventory metrics grouped by category and overall.
     *
     * @return list of inventory metrics including category-specific and overall metrics
     */
    public List<InventoryMetricsDTO> getInventoryMetrics() {
        List<Product> allProducts = productService.getAllProducts().stream()
                .filter(p -> p.getStock() > 0)
                .collect(Collectors.toList());

        Map<Long, List<Product>> groupedByCategoryId = allProducts.stream()
                .filter(p -> p.getCategory() != null && p.getCategory().getId() != null)
                .collect(Collectors.groupingBy(p -> p.getCategory().getId()));

        List<InventoryMetricsDTO> metrics = new ArrayList<>();

        for (Map.Entry<Long, List<Product>> entry : groupedByCategoryId.entrySet()) {
            Long categoryId = entry.getKey();
            List<Product> productsInCategory = entry.getValue();
            String categoryName = productsInCategory.get(0).getCategory().getName();
            double totalStock = productsInCategory.stream()
                    .mapToDouble(Product::getStock)
                    .sum();

            double totalValue = productsInCategory.stream()
                    .mapToDouble(p -> p.getPrice() * p.getStock())
                    .sum();

            double averagePrice = productsInCategory.stream()
                    .mapToDouble(Product::getPrice)
                    .average()
                    .orElse(0.0);

            InventoryMetricsDTO metric = new InventoryMetricsDTO(categoryId, categoryName, 
                totalStock, totalValue, averagePrice);
            metrics.add(metric);
        }

        // Overall metrics
        double overallTotalStock = allProducts.stream()
                .mapToDouble(Product::getStock)
                .sum();
        double overallTotalValue = allProducts.stream()
                .mapToDouble(p -> p.getPrice() * p.getStock())
                .sum();
        double overallAveragePrice = allProducts.stream()
                .mapToDouble(Product::getPrice)
                .average()
                .orElse(0.0);

        InventoryMetricsDTO overallMetrics = new InventoryMetricsDTO(0L, "Overall", 
            overallTotalStock, overallTotalValue, overallAveragePrice);

        metrics.add(overallMetrics);

        return metrics;
    }
}
