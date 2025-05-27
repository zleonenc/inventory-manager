import * as categoryService from "../categoryService";
import { CategoryDTO } from "../../types/CategoryDTO";

global.fetch = jest.fn();

describe("categoryService", () => {
    beforeEach(() => {
        (fetch as jest.Mock).mockClear();
    });

    it("getCategories returns categories", async () => {
        (fetch as jest.Mock).mockResolvedValueOnce({
            ok: true,
            json: async () => [{ id: 1, name: "Category A", active: true }],
        });

        const result = await categoryService.getCategories();
        expect(fetch).toHaveBeenCalledWith(expect.stringContaining("/api/categories"));
        expect(result).toEqual([{ id: 1, name: "Category A", active: true }]);
    });

    it("createCategory sends POST and returns savedCategory", async () => {
        const dto: CategoryDTO = { name: "Category B" };
        (fetch as jest.Mock).mockResolvedValueOnce({
            ok: true,
            json: async () => ({ id: 2, name: "Category B" }),
        });

        const result = await categoryService.createCategory(dto);
        expect(fetch).toHaveBeenCalledWith(
            expect.stringContaining("/api/categories"),
            expect.objectContaining({ method: "POST" })
        );
        expect(result).toEqual({ id: 2, name: "Category B" });
    });

    it("updateCategory sends PUT and returns updatedCategory", async () => {
        const dto: CategoryDTO = { name: "Updated Category" };
        (fetch as jest.Mock).mockResolvedValueOnce({
            ok: true,
            json: async () => ({ id: 1, name: "Updated Category" }),
        });

        const result = await categoryService.updateCategory(1, dto);
        expect(fetch).toHaveBeenCalledWith(
            expect.stringContaining("/api/categories/1"),
            expect.objectContaining({ method: "PUT" })
        );
        expect(result).toEqual({ id: 1, name: "Updated Category" });
    });

    it("deleteCategory sends DELETE", async () => {
        (fetch as jest.Mock).mockResolvedValueOnce({ ok: true });

        await categoryService.deleteCategory(1);
        expect(fetch).toHaveBeenCalledWith(expect.stringContaining("/api/categories/1"), expect.objectContaining({ method: "DELETE" }));
    });

    it("throws on failed fetch", async () => {
        (fetch as jest.Mock).mockResolvedValueOnce({ ok: false });
        await expect(categoryService.getCategories()).rejects.toThrow("Failed to fetch categories");
    });
});