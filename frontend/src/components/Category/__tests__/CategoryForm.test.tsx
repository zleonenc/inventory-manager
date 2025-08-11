import React from "react";
import {
    render,
    screen,
    fireEvent,
    waitFor
} from "@testing-library/react";

import CategoryForm from "../CategoryForm";

let mockCategoryCtx: any = { categories: [], fetchCategories: jest.fn() };
jest.mock("../../../context/CategoryContext", () => ({
    useCategoryContext: () => mockCategoryCtx,
}));

describe("CategoryForm", () => {
    beforeEach(() => {
        mockCategoryCtx = { categories: [], fetchCategories: jest.fn() };
    });

    it("shows error when name is empty", () => {
        const onSubmit = jest.fn();
        const onClose = jest.fn();

        render(
            <CategoryForm open title="Create Category" onSubmit={onSubmit} onClose={onClose} />
        );

        fireEvent.click(screen.getByRole("button", { name: /save/i }));

        expect(screen.getByText(/Name is required/i)).toBeInTheDocument();
        expect(onSubmit).not.toHaveBeenCalled();
    });

    it("prevents duplicate names", () => {
        mockCategoryCtx = {
            categories: [
                { id: 1, name: "Category A", active: true },
                { id: 2, name: "Category B", active: true },
            ],
            fetchCategories: jest.fn(),
        };

        const onSubmit = jest.fn();
        const onClose = jest.fn();

        render(
            <CategoryForm open title="Edit Category" onSubmit={onSubmit} onClose={onClose} />
        );

        fireEvent.change(screen.getByLabelText(/name/i), { target: { value: "category a" } });
        fireEvent.click(screen.getByRole("button", { name: /save/i }));

        expect(screen.getByText(/already exists/i)).toBeInTheDocument();
        expect(onSubmit).not.toHaveBeenCalled();
    });

    it("submits and optionally returns id", async () => {
        const onSubmit = jest.fn().mockResolvedValue({ id: 42 });
        const onClose = jest.fn();
        const onCreatedId = jest.fn();

        render(
            <CategoryForm open title="Create Category" onSubmit={onSubmit} onClose={onClose} onCreatedId={onCreatedId} />
        );

        fireEvent.change(screen.getByLabelText(/name/i), { target: { value: "Unique" } });
        fireEvent.click(screen.getByRole("button", { name: /save/i }));

        await waitFor(() => expect(onSubmit).toHaveBeenCalled());
        expect(onSubmit).toHaveBeenCalledWith("Unique");
        expect(onCreatedId).toHaveBeenCalledWith(42);
        expect(onClose).toHaveBeenCalled();
    });
});
