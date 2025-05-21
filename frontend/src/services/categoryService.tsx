import { Category } from "../types/Category";
import { CategoryDTO } from "../types/CategoryDTO";

const API_URL = "http://localhost:9090/api/categories";

export const getCategories = async (): Promise<Category[]> => {
    const response = await fetch(API_URL);
    if (!response.ok) {
        throw new Error("Failed to fetch categories");
    }
    return response.json();
};

export const createCategory = async (categoryDTO: CategoryDTO): Promise<Category> => {
    const response = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(categoryDTO),
    });
    if (!response.ok) {
        throw new Error("Failed to create category");
    }
    return response.json();
};

export const updateCategory = async (id: number, categoryDTO: CategoryDTO): Promise<Category> => {
    const response = await fetch(`${API_URL}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(categoryDTO),
    });
    if (!response.ok) {
        throw new Error("Failed to update category");
    }
    return response.json();
};

export const deleteCategory = async (id: number): Promise<void> => {
    const response = await fetch(`${API_URL}/${id}`, { method: "DELETE" });
    if (!response.ok) {
        throw new Error("Failed to delete category");
    }
};