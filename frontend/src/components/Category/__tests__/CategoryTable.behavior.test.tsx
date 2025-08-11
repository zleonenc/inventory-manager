import React from "react";
import {
    render,
    screen,
    fireEvent,
    waitFor
} from "@testing-library/react";

import CategoryTable from "../CategoryTable";

let mockCategoryCtx: any = {
    categories: [{ id: 1, name: "Category A", active: true }],
    editCategory: jest.fn().mockResolvedValue(undefined),
    removeCategory: jest.fn().mockResolvedValue(undefined),
};
jest.mock("../../../context/CategoryContext", () => ({
    useCategoryContext: () => mockCategoryCtx,
}));

describe("CategoryTable behavior", () => {
    beforeEach(() => {
        mockCategoryCtx.editCategory.mockClear();
        mockCategoryCtx.removeCategory.mockClear();
    });

    it("opens delete dialog then cancel closes it", async () => {
        render(<CategoryTable />);

        fireEvent.click(screen.getByRole("button", { name: /delete category category a/i }));
        expect(await screen.findByText(/Are you sure you want to delete/i)).toBeInTheDocument();

        fireEvent.click(screen.getByRole("button", { name: /cancel/i }));

        await waitFor(() => {
            expect(screen.queryByText(/Are you sure you want to delete/i)).not.toBeInTheDocument();
        });
        expect(mockCategoryCtx.removeCategory).not.toHaveBeenCalled();
    });

    it("confirms delete and calls removeCategory", async () => {
        render(<CategoryTable />);

        fireEvent.click(screen.getByRole("button", { name: /delete category category a/i }));
        fireEvent.click(await screen.findByRole("button", { name: /^delete$/i }));

        await waitFor(() => expect(mockCategoryCtx.removeCategory).toHaveBeenCalledWith(1));
    });
});
