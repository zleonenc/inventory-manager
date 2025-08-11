import React from "react";

import {
    render,
    screen,
    fireEvent
} from "@testing-library/react";

import ProductFilter from "../ProductFilter";

let mockProductCtx: any = { fetchProducts: jest.fn(), setLastFilters: jest.fn() };
let mockCategoryCtx: any = { categories: [{ id: 1, name: "Cat A" }, { id: 2, name: "Cat B" }] };
jest.mock("../../../context/ProductContext", () => ({
    useProductContext: () => mockProductCtx,
}));
jest.mock("../../../context/CategoryContext", () => ({
    useCategoryContext: () => mockCategoryCtx,
}));

describe("ProductFilter behavior", () => {
    beforeEach(() => {
        mockProductCtx = { fetchProducts: jest.fn(), setLastFilters: jest.fn() };
        mockCategoryCtx = { categories: [{ id: 1, name: "Cat A" }, { id: 2, name: "Cat B" }] };
    });

    it("calls setLastFilters with composed filters (table effect will fetch)", () => {
        render(<ProductFilter />);

        fireEvent.change(screen.getByLabelText(/name/i), { target: { value: "abc" } });

        // select second category (value 2)
        const categorySelect = screen.getByLabelText(/category/i);
        fireEvent.mouseDown(categorySelect);
        const option = screen.getByRole("option", { name: "Cat B" });
        fireEvent.click(option);

        // choose Availability = In Stock
        const availabilitySelect = screen.getByLabelText(/availability/i);
        fireEvent.mouseDown(availabilitySelect);
        fireEvent.click(screen.getByRole("option", { name: /in stock/i }));

        fireEvent.click(screen.getByText(/search/i));

        expect(mockProductCtx.setLastFilters).toHaveBeenCalledWith({ name: "abc", categories: [2], available: "instock" });
        expect(mockProductCtx.fetchProducts).not.toHaveBeenCalled();
    });
});
