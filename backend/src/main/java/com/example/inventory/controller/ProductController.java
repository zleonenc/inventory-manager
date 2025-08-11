package com.example.inventory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import com.example.inventory.service.ProductService;
import com.example.inventory.service.InventoryMetricsService;
import com.example.inventory.dto.InventoryMetricsDTO;
import com.example.inventory.dto.ProductDTO;
import com.example.inventory.dto.PagedResponse;
import com.example.inventory.model.Product;

@RestController
@RequestMapping("/api/products")
@Validated
@Tag(name = "Products", description = "Operations related to products")
public class ProductController {
    private final ProductService productService;
    private final InventoryMetricsService inventoryMetricsService;

    public ProductController(ProductService productService, InventoryMetricsService inventoryMetricsService) {
        this.productService = productService;
        this.inventoryMetricsService = inventoryMetricsService;
    }

    // Create a product: POST
    @PostMapping()
    @Operation(summary = "Create a new product")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Invalid product payload", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        Product product = productService.saveFromDTO(productDTO);
        return ResponseEntity.ok(product);
    }

    // Get filtered products: GET
    @GetMapping()
    @Operation(summary = "Get filtered, sorted, and paginated list of products")
    public ResponseEntity<PagedResponse<Product>> getFilteredSortedProducts(
            @Parameter(description = "Product name contains filter", example = "milk") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by category IDs", array = @ArraySchema(schema = @Schema(implementation = Long.class)), example = "1,2") @RequestParam(required = false) List<Long> categories,
            @Parameter(description = "Availability filter", schema = @Schema(allowableValues = { "instock",
                    "outofstock" }), example = "instock") @RequestParam(required = false) String available,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Primary sort field", schema = @Schema(allowableValues = { "name", "price",
                    "expirationdate", "stock",
                    "category" }), example = "name") @RequestParam(required = false) String primarySortBy,
            @Parameter(description = "Primary sort direction", schema = @Schema(allowableValues = { "asc",
                    "desc" }), example = "asc") @RequestParam(required = false, defaultValue = "asc") String primarySortDirection,
            @Parameter(description = "Secondary sort field", schema = @Schema(allowableValues = { "name", "price",
                    "expirationdate", "stock",
                    "category" }), example = "price") @RequestParam(required = false) String secondarySortBy,
            @Parameter(description = "Secondary sort direction", schema = @Schema(allowableValues = { "asc",
                    "desc" }), example = "asc") @RequestParam(required = false, defaultValue = "asc") String secondarySortDirection) {
        PagedResponse<Product> response = productService.getFilteredSortedProducts(
                name, categories, available, page, size, primarySortBy, primarySortDirection, secondarySortBy,
                secondarySortDirection);
        return ResponseEntity.ok(response);
    }

    // Update a product: PUT
    @PutMapping("/{id}")
    @Operation(summary = "Update a product by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "400", description = "Invalid product payload", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        Product updatedProduct = productService.updateProductById(id, dto);
        return ResponseEntity.ok(updatedProduct);
    }

    // Mark product as outofstock
    // @PUT outofstock
    @PutMapping("/{id}/outofstock")
    @Operation(summary = "Mark a product as out of stock")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Product> markProductAsOutOfStock(@PathVariable Long id) {
        Product updatedProduct = productService.markProductAsOutOfStock(id);
        return ResponseEntity.ok(updatedProduct);
    }

    // Mark product as instock
    // @PUT instock
    @PutMapping("/{id}/instock")
    @Operation(summary = "Mark a product as in stock")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Product> markProductAsInStock(@PathVariable Long id) {
        Product updatedProduct = productService.markProductAsInStock(id);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    // Clear all products: DELETE
    @DeleteMapping("/clear")
    @Operation(summary = "Delete all products")
    public ResponseEntity<Void> clearProducts() {
        productService.clearProducts();
        return ResponseEntity.noContent().build();
    }

    // Get all metrics: GET
    @GetMapping("/metrics")
    @Operation(summary = "Get inventory metrics aggregated by category and overall")
    public ResponseEntity<List<InventoryMetricsDTO>> getInventoryMetrics() {
        List<InventoryMetricsDTO> metrics = inventoryMetricsService.getInventoryMetrics();
        return ResponseEntity.ok(metrics);
    }
}
