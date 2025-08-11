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
    Product
} from "../types/Product";
import {
    ProductDTO
} from "../types/ProductDTO";
import {
    Metric
} from "../types/Metric";

export interface ProductPagedResponse {
    content: Product[];
    totalElements: number;
}

export const getProducts = async (params: Record<string, any> = {}): Promise<ProductPagedResponse> =>
    get<ProductPagedResponse>(`${ENDPOINTS.PRODUCTS}`, params);

export const createProduct = async (product: ProductDTO): Promise<Product> =>
    post<Product>(`${ENDPOINTS.PRODUCTS}`, product);

export const updateProduct = async (id: number, product: ProductDTO): Promise<Product> =>
    put<Product>(`${ENDPOINTS.PRODUCTS}/${id}`, product);

export const setProductInStock = async (id: number): Promise<Product> =>
    put<Product>(`${ENDPOINTS.PRODUCTS}/${id}/instock`);

export const setProductOutOfStock = async (id: number): Promise<Product> =>
    put<Product>(`${ENDPOINTS.PRODUCTS}/${id}/outofstock`);

export const deleteProduct = async (id: number): Promise<void> => del(`${ENDPOINTS.PRODUCTS}/${id}`);

export const getInventoryMetrics = async (): Promise<Metric[]> => get<Metric[]>(`${ENDPOINTS.PRODUCTS}/metrics`);
