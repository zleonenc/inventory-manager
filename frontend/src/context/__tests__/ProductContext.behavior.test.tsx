import React,
{
    useEffect,
    useRef
} from "react";

import {
    render,
    screen,
    waitFor
} from "@testing-library/react";

import {
    ProductProvider,
    useProductContext
} from "../ProductContext";

import * as productService from "../../services/productService";

jest.mock("../../services/productService");

const mockGetProducts = productService.getProducts as jest.Mock;
const mockGetMetrics = productService.getInventoryMetrics as jest.Mock;
const mockCreate = productService.createProduct as jest.Mock;
const mockUpdate = productService.updateProduct as jest.Mock;
const mockInStock = productService.setProductInStock as jest.Mock;
const mockOutOfStock = productService.setProductOutOfStock as jest.Mock;
const mockDelete = productService.deleteProduct as jest.Mock;

function Consumer() {
    const {
        products,
        metrics,
        fetchProducts,
        createProduct,
        updateProduct,
        setProductInStock,
        setProductOutOfStock,
        deleteProduct,
    } = useProductContext();

    // expose for assertions
    useEffect(() => {
        (window as any).__ctx = {
            products,
            metrics,
            fetchProducts,
            createProduct,
            updateProduct,
            setProductInStock,
            setProductOutOfStock,
            deleteProduct,
        };
    }, [products, metrics, fetchProducts, createProduct, updateProduct, setProductInStock, setProductOutOfStock, deleteProduct]);

    return (
        <div>
            <div data-testid="products-count">{products.length}</div>
            <div data-testid="metrics-count">{metrics.length}</div>
        </div>
    );
}

describe("ProductContext behavior", () => {
    beforeEach(() => {
        jest.clearAllMocks();
        mockGetProducts.mockResolvedValue({ content: [], totalElements: 0 });
        mockGetMetrics.mockResolvedValue([]);
    });

    it("initializes by fetching metrics only (table initiates product fetch)", async () => {
        render(
            <ProductProvider>
                <Consumer />
            </ProductProvider>
        );

        await waitFor(() => expect(mockGetProducts).toHaveBeenCalledTimes(0));
        await waitFor(() => expect(mockGetMetrics).toHaveBeenCalledTimes(1));

        expect(screen.getByTestId("products-count").textContent).toBe("0");
        expect(screen.getByTestId("metrics-count").textContent).toBe("0");
    });

    it("refreshes metrics after create/update/stock/delete", async () => {
        mockCreate.mockResolvedValue({ id: 1, name: "P1", price: 1, available: true, category: { id: 1, name: "C" } });
        mockUpdate.mockResolvedValue({ id: 1, name: "P1u", price: 2, available: true, category: { id: 1, name: "C" } });
        mockInStock.mockResolvedValue({ id: 1, name: "P1", price: 1, available: true, category: { id: 1, name: "C" } });
        mockOutOfStock.mockResolvedValue({ id: 1, name: "P1", price: 1, available: false, category: { id: 1, name: "C" } });
        mockDelete.mockResolvedValue(undefined);

        render(
            <ProductProvider>
                <Consumer />
            </ProductProvider>
        );

        await waitFor(() => expect(mockGetProducts).toHaveBeenCalledTimes(0));
        await waitFor(() => expect(mockGetMetrics).toHaveBeenCalledTimes(1));

        const ctx = () => (window as any).__ctx;

        await ctx().createProduct({ name: "x", price: 1, available: true, categoryId: 1 });
        await waitFor(() => expect(mockGetMetrics).toHaveBeenCalledTimes(2));

        await ctx().updateProduct(1, { name: "y", price: 2, available: true, categoryId: 1 });
        await waitFor(() => expect(mockGetMetrics).toHaveBeenCalledTimes(3));

        await ctx().setProductInStock(1);
        await waitFor(() => expect(mockGetMetrics).toHaveBeenCalledTimes(4));

        await ctx().setProductOutOfStock(1);
        await waitFor(() => expect(mockGetMetrics).toHaveBeenCalledTimes(5));

        await ctx().deleteProduct(1);
        await waitFor(() => expect(mockGetMetrics).toHaveBeenCalledTimes(6));
    });

    it("exposes stable function references across state updates", async () => {
        render(
            <ProductProvider>
                <Consumer />
            </ProductProvider>
        );

        await waitFor(() => expect(mockGetProducts).toHaveBeenCalledTimes(0));

        const first = (window as any).__ctx;

        // trigger a state update by fetching with same filters
        await first.fetchProducts({});

        const second = (window as any).__ctx;

        expect(second.fetchProducts).toBe(first.fetchProducts);
        expect(second.createProduct).toBe(first.createProduct);
        expect(second.updateProduct).toBe(first.updateProduct);
        expect(second.setProductInStock).toBe(first.setProductInStock);
        expect(second.setProductOutOfStock).toBe(first.setProductOutOfStock);
        expect(second.deleteProduct).toBe(first.deleteProduct);
    });
});
