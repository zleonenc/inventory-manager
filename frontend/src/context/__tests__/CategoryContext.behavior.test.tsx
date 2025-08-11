import React from "react";
import {
    render,
    waitFor
} from "@testing-library/react";

import {
    CategoryProvider,
    useCategoryContext
} from "../CategoryContext";

import * as categoryService from "../../services/categoryService";

jest.mock("../../services/categoryService");

const mockGet = categoryService.getCategories as jest.Mock;
const mockCreate = categoryService.createCategory as jest.Mock;
const mockUpdate = categoryService.updateCategory as jest.Mock;
const mockDelete = categoryService.deleteCategory as jest.Mock;

function Consumer() {
    const { fetchCategories, addCategory, editCategory, removeCategory } = useCategoryContext();
    (window as any).__cat = { fetchCategories, addCategory, editCategory, removeCategory };
    return null;
}

describe("CategoryContext behavior", () => {
    beforeEach(() => {
        jest.clearAllMocks();
        mockGet.mockResolvedValue([]);
        mockCreate.mockResolvedValue({ id: 1, name: "C1" });
        mockUpdate.mockResolvedValue({ id: 1, name: "C1u" });
        mockDelete.mockResolvedValue(undefined);
    });

    it("initializes by fetching categories", async () => {
        render(
            <CategoryProvider>
                <Consumer />
            </CategoryProvider>
        );

        await waitFor(() => expect(mockGet).toHaveBeenCalledTimes(1));
    });

    it("re-fetches after add/edit/remove", async () => {
        render(
            <CategoryProvider>
                <Consumer />
            </CategoryProvider>
        );

        await waitFor(() => expect(mockGet).toHaveBeenCalledTimes(1));

        const ctx = () => (window as any).__cat;

        await ctx().addCategory({ name: "X" });
        await waitFor(() => expect(mockGet).toHaveBeenCalledTimes(2));

        await ctx().editCategory(1, { name: "Y" });
        await waitFor(() => expect(mockGet).toHaveBeenCalledTimes(3));

        await ctx().removeCategory(1);
        await waitFor(() => expect(mockGet).toHaveBeenCalledTimes(4));
    });

    it("exposes stable function references across state updates", async () => {
        render(
            <CategoryProvider>
                <Consumer />
            </CategoryProvider>
        );

        await waitFor(() => expect(mockGet).toHaveBeenCalledTimes(1));

        const first = (window as any).__cat;
        await first.fetchCategories();
        const second = (window as any).__cat;

        expect(second.fetchCategories).toBe(first.fetchCategories);
        expect(second.addCategory).toBe(first.addCategory);
        expect(second.editCategory).toBe(first.editCategory);
        expect(second.removeCategory).toBe(first.removeCategory);
    });
});
