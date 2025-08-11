import {
    del,
    get,
    post,
    put
} from "./apiClient";

import {
    ENDPOINTS
} from "./endpoints";
import {
    Category
} from "../types/Category";
import {
    CategoryDTO
} from "../types/CategoryDTO";

export const getCategories = async (): Promise<Category[]> => get<Category[]>(`${ENDPOINTS.CATEGORIES}`);

export const createCategory = async (categoryDTO: CategoryDTO): Promise<Category> => post<Category>(`${ENDPOINTS.CATEGORIES}`, categoryDTO);

export const updateCategory = async (id: number, categoryDTO: CategoryDTO): Promise<Category> =>
    put<Category>(`${ENDPOINTS.CATEGORIES}/${id}`, categoryDTO);

export const deleteCategory = async (id: number): Promise<void> => del(`${ENDPOINTS.CATEGORIES}/${id}`);
