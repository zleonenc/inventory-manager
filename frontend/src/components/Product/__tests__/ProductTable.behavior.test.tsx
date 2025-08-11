import React from "react";

import {
    render,
    screen,
    fireEvent,
    waitFor
} from "@testing-library/react";

import ProductTable from "../ProductTable";

let mockProductCtx: any = {
    products: [
        { id: 1, name: "P1", price: 10, stock: 0, category: { id: 1, name: "Cat A" }, creationDate: "", updateDate: "", expirationDate: null, active: true },
        { id: 2, name: "P2", price: 20, stock: 5, category: { id: 2, name: "Cat B" }, creationDate: "", updateDate: "", expirationDate: null, active: true },
    ],
    total: 20,
    fetchProducts: jest.fn().mockResolvedValue(undefined),
    deleteProduct: jest.fn().mockResolvedValue(undefined),
    setProductInStock: jest.fn().mockResolvedValue(undefined),
    setProductOutOfStock: jest.fn().mockResolvedValue(undefined),
    lastFilters: {},
};
jest.mock("../../../context/ProductContext", () => ({
    useProductContext: () => mockProductCtx,
}));

// Category context isn't used directly here but table rows may rely on categories in row rendering in other components
jest.mock("../../../context/CategoryContext", () => ({
    useCategoryContext: () => ({ categories: [] }),
}));

describe("ProductTable behavior", () => {
    beforeEach(() => {
        mockProductCtx.fetchProducts.mockClear();
        mockProductCtx.deleteProduct.mockClear();
    });

    it("renders pagination and rows per page control", () => {
        render(<ProductTable />);
        expect(screen.getByLabelText(/Products pagination/i)).toBeInTheDocument();
        const rowsPerPageControls = screen.getAllByLabelText(/Rows per page/i);
        expect(rowsPerPageControls.length).toBeGreaterThan(0);
    });

    it("opens delete dialog and confirms deletion", async () => {
        render(<ProductTable />);

        const deleteButtons = screen.getAllByRole("button", { name: /delete/i });
        // The first 'Delete' might be header or dialog; use the row delete button which should exist twice
        fireEvent.click(deleteButtons[0]);

        expect(await screen.findByText(/Are you sure you want to delete/i)).toBeInTheDocument();

        fireEvent.click(screen.getByRole("button", { name: /^delete$/i }));

        await waitFor(() => expect(mockProductCtx.deleteProduct).toHaveBeenCalled());
        // After confirming, table refreshes
        expect(mockProductCtx.fetchProducts).toHaveBeenCalled();
    });
});
