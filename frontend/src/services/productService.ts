import { Product } from "../types/Product";

const API_URL = "http://localhost:8080/api/products";

export const getProducts = async (): Promise<Product[]> => {
    const response = await fetch(API_URL);
    if (!response.ok) {
        throw new Error("Failed to fetch products");
    }
    return response.json();
};