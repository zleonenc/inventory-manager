import React from "react";

import {
    render,
    screen,
    fireEvent,
    waitFor
} from "@testing-library/react";

import ProductForm from "../ProductForm";

// Dynamic mocks for contexts (must start with 'mock' for Jest out-of-scope rule)
let mockProductCtx: any = { fetchProducts: jest.fn() };
let mockCategoryCtx: any = { categories: [], fetchCategories: jest.fn() };
jest.mock("../../../context/ProductContext", () => ({
    useProductContext: () => mockProductCtx,
}));
jest.mock("../../../context/CategoryContext", () => ({
    useCategoryContext: () => mockCategoryCtx,
}));

describe("ProductForm", () => {
    beforeEach(() => {
        mockProductCtx = { fetchProducts: jest.fn() };
        mockCategoryCtx = { categories: [], fetchCategories: jest.fn() };
    });

    it("shows validation errors when required fields missing", async () => {
        const onSubmit = jest.fn();
        const onClose = jest.fn();

        render(
            <ProductForm open title="Create Product" onSubmit={onSubmit} onClose={onClose} />
        );

        fireEvent.click(screen.getByRole("button", { name: /save/i }));

        expect(await screen.findByText(/Please fix the highlighted fields/i)).toBeInTheDocument();
        expect(onSubmit).not.toHaveBeenCalled();
    });

    it("submits with valid values and refreshes products", async () => {
        mockCategoryCtx = {
            categories: [
                { id: 10, name: "Cat A" },
                { id: 20, name: "Cat B" },
            ],
            fetchCategories: jest.fn(),
        };

        const onSubmit = jest.fn().mockResolvedValue(undefined);
        const onClose = jest.fn();
        mockProductCtx = { fetchProducts: jest.fn().mockResolvedValue(undefined) };

        render(
            <ProductForm open title="Create Product" onSubmit={onSubmit} onClose={onClose} />
        );

        fireEvent.change(screen.getByLabelText(/name/i), { target: { value: "New Prod" } });
        // Category defaults to first when provided; keep it
        fireEvent.change(screen.getByLabelText(/stock/i), { target: { value: "5" } });
        fireEvent.change(screen.getByLabelText(/unit price/i), { target: { value: "12.5" } });
        fireEvent.change(screen.getByLabelText(/expiration date/i), { target: { value: "2025-01-01" } });

        fireEvent.click(screen.getByRole("button", { name: /save/i }));

        await waitFor(() => expect(onSubmit).toHaveBeenCalled());
        expect(onSubmit).toHaveBeenCalledWith({
            name: "New Prod",
            categoryId: 10,
            stock: 5,
            price: 12.5,
            expirationDate: "2025-01-01",
        });
        expect(mockProductCtx.fetchProducts).toHaveBeenCalled();
        expect(onClose).toHaveBeenCalled();
    });
});
