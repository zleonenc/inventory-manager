import * as productService from "../productService";
import { ProductDTO } from "../../types/ProductDTO";

global.fetch = jest.fn();

describe("productService", () => {
    beforeEach(() => {
        (fetch as jest.Mock).mockClear();
    });

    it("getProducts returns products", async () => {
        const mockResponse = {
            content: [
                { id: 2, name: "Product A", categoryId: 1, stock: 5, price: 20, expirationDate: "2025-01-01" },
                { id: 1, name: "Product B", categoryId: 2, stock: 10, price: 10, expirationDate: null }
            ],
            totalElements: 2
        };
        (fetch as jest.Mock).mockResolvedValueOnce({
            ok: true,
            json: async () => mockResponse,
        });

        const params = {
            name: "Product",
            categories: [1, 2],
            available: "instock",
            page: 0,
            size: 2,
            primarySortBy: "name",
            primarySortDirection: "asc",
            secondarySortBy: null,
            secondarySortDirection: null
        };

        const result = await productService.getProducts(params);
        expect(fetch).toHaveBeenCalledWith(
            expect.stringContaining("/api/products?name=Product&categories=1%2C2&available=instock&page=0&size=2&primarySortBy=name&primarySortDirection=asc&secondarySortBy=null&secondarySortDirection=null"),
        );
        expect(result).toEqual(mockResponse);
    });

    it("createProduct sends POST and returns savedProduct", async () => {
        const dto: ProductDTO = { name: "Product A", categoryId: 1, stock: 10, price: 10, expirationDate: null };
        (fetch as jest.Mock).mockResolvedValueOnce({
            ok: true,
            json: async () => ({ id: 1, ...dto }),
        });

        const result = await productService.createProduct(dto);
        expect(fetch).toHaveBeenCalledWith(
            expect.stringContaining("/api/products"),
            expect.objectContaining({ method: "POST" })
        );
        expect(result).toEqual({ id: 1, ...dto });
    });

    it("updateProduct sends PUT and returns updatedProduct", async () => {
        const dto: ProductDTO = { name: "Updated Product", categoryId: 2, stock: 5, price: 20, expirationDate: "2025-01-01" };
        (fetch as jest.Mock).mockResolvedValueOnce({
            ok: true,
            json: async () => ({ id: 1, ...dto }),
        });

        const result = await productService.updateProduct(1, dto);
        expect(fetch).toHaveBeenCalledWith(
            expect.stringContaining("/api/products/1"),
            expect.objectContaining({ method: "PUT" })
        );
        expect(result).toEqual({ id: 1, ...dto });
    });

    it("deleteProduct sends DELETE", async () => {
        (fetch as jest.Mock).mockResolvedValueOnce({ ok: true });

        await productService.deleteProduct(1);
        expect(fetch).toHaveBeenCalledWith(
            expect.stringContaining("/api/products/1"),
            expect.objectContaining({ method: "DELETE" })
        );
    });

    it("setProductInStock sends POST and returns product", async () => {
        (fetch as jest.Mock).mockResolvedValueOnce({
            ok: true,
            json: async () => ({ id: 1, stock: 10 }),
        });

        const result = await productService.setProductInStock(1);
        expect(fetch).toHaveBeenCalledWith(
            expect.stringContaining("/api/products/1/instock"),
            expect.objectContaining({ method: "POST" })
        );
        expect(result).toEqual({ id: 1, stock: 10 });
    });

    it("setProductOutOfStock sends POST and returns product", async () => {
        (fetch as jest.Mock).mockResolvedValueOnce({
            ok: true,
            json: async () => ({ id: 1, stock: 0 }),
        });

        const result = await productService.setProductOutOfStock(1);
        expect(fetch).toHaveBeenCalledWith(
            expect.stringContaining("/api/products/1/outofstock"),
            expect.objectContaining({ method: "POST" })
        );
        expect(result).toEqual({ id: 1, stock: 0 });
    });

    it("getInventoryMetrics returns metrics", async () => {
        const mockMetrics = [
            { label: "Category A", totalStock: 5, totalValue: 50, averagePrice: 10 },
            { label: "Category B", totalStock: 3, totalValue: 30, averagePrice: 10 },
            { label: "Overall", totalStock: 8, totalValue: 80, averagePrice: 10 }
        ];
        (fetch as jest.Mock).mockResolvedValueOnce({
            ok: true,
            json: async () => mockMetrics,
        });

        const result = await productService.getInventoryMetrics();
        expect(fetch).toHaveBeenCalledWith(expect.stringContaining("/api/products/metrics"));
        expect(result).toEqual(mockMetrics);
    });

    it("throws on failed fetch", async () => {
        (fetch as jest.Mock).mockResolvedValueOnce({ ok: false });
        await expect(productService.getProducts()).rejects.toThrow("Failed to fetch products");
    });
});