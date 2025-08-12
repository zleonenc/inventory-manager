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

    public ProductController(final ProductService productService,
            final InventoryMetricsService inventoryMetricsService) {
        this.productService = productService;
        this.inventoryMetricsService = inventoryMetricsService;
    }

    /**
     * Creates a new product from the provided DTO.
     *
     * @param productDTO the product data transfer object
     * @return the created product wrapped in ResponseEntity
     */
    @PostMapping()
    @Operation(summary = "Create a new product")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Invalid product payload", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Product> createProduct(
            @Valid @RequestBody final ProductDTO productDTO) {
        Product product = productService.saveFromDTO(productDTO);
        return ResponseEntity.ok(product);
    }

    /**
     * Retrieves filtered, sorted, and paginated list of products.
     *
     * @param name                   the product name filter (optional)
     * @param categories             the list of category IDs to filter by
     *                               (optional)
     * @param available              the availability filter (optional)
     * @param page                   the page number (0-based)
     * @param size                   the page size
     * @param primarySortBy          the primary sort field (optional)
     * @param primarySortDirection   the primary sort direction
     * @param secondarySortBy        the secondary sort field (optional)
     * @param secondarySortDirection the secondary sort direction
     * @return the paginated list of filtered products wrapped in ResponseEntity
     */
    @GetMapping()
    @Operation(summary = "Get filtered, sorted, and paginated list of products")
    public ResponseEntity<PagedResponse<Product>> getFilteredSortedProducts(
            @Parameter(description = "Product name contains filter", example = "milk") @RequestParam(required = false) final String name,
            @Parameter(description = "Filter by category IDs", array = @ArraySchema(schema = @Schema(implementation = Long.class)), example = "1,2") @RequestParam(required = false) final List<Long> categories,
            @Parameter(description = "Availability filter", schema = @Schema(allowableValues = { "instock",
                    "outofstock" }), example = "instock") @RequestParam(required = false) final String available,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") final int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") final int size,
            @Parameter(description = "Primary sort field", schema = @Schema(allowableValues = { "name", "price",
                    "expirationdate",
                    "stock",
                    "category" }), example = "name") @RequestParam(required = false) final String primarySortBy,
            @Parameter(description = "Primary sort direction", schema = @Schema(allowableValues = { "asc",
                    "desc" }), example = "asc") @RequestParam(required = false, defaultValue = "asc") final String primarySortDirection,
            @Parameter(description = "Secondary sort field", schema = @Schema(allowableValues = { "name", "price",
                    "expirationdate",
                    "stock",
                    "category" }), example = "price") @RequestParam(required = false) final String secondarySortBy,
            @Parameter(description = "Secondary sort direction", schema = @Schema(allowableValues = { "asc",
                    "desc" }), example = "asc") @RequestParam(required = false, defaultValue = "asc") final String secondarySortDirection) {
        PagedResponse<Product> response = productService.getFilteredSortedProducts(
                name, categories, available, page, size, primarySortBy,
                primarySortDirection, secondarySortBy, secondarySortDirection);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing product by ID.
     *
     * @param id  the ID of the product to update
     * @param dto the updated product data transfer object
     * @return the updated product wrapped in ResponseEntity
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a product by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "400", description = "Invalid product payload", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Product> updateProduct(@PathVariable final Long id,
            @Valid @RequestBody final ProductDTO dto) {
        Product updatedProduct = productService.updateProductById(id, dto);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Marks a product as out of stock.
     *
     * @param id the ID of the product to mark as out of stock
     * @return the updated product wrapped in ResponseEntity
     */
    @PutMapping("/{id}/outofstock")
    @Operation(summary = "Mark a product as out of stock")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Product> markProductAsOutOfStock(
            @PathVariable final Long id) {
        Product updatedProduct = productService.markProductAsOutOfStock(id);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Marks a product as in stock.
     *
     * @param id the ID of the product to mark as in stock
     * @return the updated product wrapped in ResponseEntity
     */
    @PutMapping("/{id}/instock")
    @Operation(summary = "Mark a product as in stock")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Product> markProductAsInStock(@PathVariable final Long id) {
        Product updatedProduct = productService.markProductAsInStock(id);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Deletes a product by ID.
     *
     * @param id the ID of the product to delete
     * @return empty ResponseEntity with no content status
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable final Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Clears all products from the system.
     *
     * @return empty ResponseEntity with no content status
     */
    @DeleteMapping("/clear")
    @Operation(summary = "Delete all products")
    public ResponseEntity<Void> clearProducts() {
        productService.clearProducts();
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves inventory metrics aggregated by category and overall.
     *
     * @return list of inventory metrics wrapped in ResponseEntity
     */
    @GetMapping("/metrics")
    @Operation(summary = "Get inventory metrics aggregated by category and overall")
    public ResponseEntity<List<InventoryMetricsDTO>> getInventoryMetrics() {
        List<InventoryMetricsDTO> metrics = inventoryMetricsService.getInventoryMetrics();
        return ResponseEntity.ok(metrics);
    }
}
