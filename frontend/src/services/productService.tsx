import { Product } from "../types/Product";
import { ProductDTO } from "../types/ProductDTO";
import { Metric } from "../types/Metric";

const API_URL = "http://localhost:9090/api/products";

export interface ProductPagedResponse {
    content: Product[];
    totalElements: number;
}

export const getProducts = async (params: Record<string, any> = {}): Promise<ProductPagedResponse> => {
    const query = new URLSearchParams(params).toString();
    const response = await fetch(`${API_URL}?${query}`);
    if (!response.ok) throw new Error("Failed to fetch products");
    return response.json();
};

export const createProduct = async (product: ProductDTO): Promise<Product> => {
    const response = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(product),
    });
    if (!response.ok) {
        throw new Error("Failed to create product");
    }
    return response.json();
};

export const updateProduct = async (id: number, product: ProductDTO): Promise<Product> => {
    const response = await fetch(`${API_URL}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(product),
    });
    if (!response.ok) {
        throw new Error("Failed to update product");
    }
    return response.json();
};

export const setProductInStock = async (id: number): Promise<Product> => {
    const response = await fetch(`${API_URL}/${id}/instock`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
    });
    if (!response.ok) {
        throw new Error("Failed to set product in stock");
    }
    return response.json();
}

export const setProductOutOfStock = async (id: number): Promise<Product> => {
    const response = await fetch(`${API_URL}/${id}/outofstock`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
    });
    if (!response.ok) {
        throw new Error("Failed to set product out of stock");
    }
    return response.json();
}

export const deleteProduct = async (id: number): Promise<void> => {
    const response = await fetch(`${API_URL}/${id}`, { method: "DELETE" });
    if (!response.ok) {
        throw new Error("Failed to delete product");
    }
};

export const getInventoryMetrics = async (): Promise<Metric[]> => {
    const response = await fetch(`${API_URL}/metrics`);
    if (!response.ok) {
        throw new Error("Failed to fetch inventory metrics");
    }
    return response.json();
};