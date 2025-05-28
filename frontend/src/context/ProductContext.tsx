import { createContext, useContext, useEffect, useState, ReactNode, useCallback } from "react";

import { getProducts as apiGetProducts, getInventoryMetrics as apiGetInventoryMetrics, createProduct as apiCreateProduct, updateProduct as apiUpdateProduct, setProductInStock as apiSetProductInStock, setProductOutOfStock as apiSetProductOutOfStock, deleteProduct as apiDeleteProduct } from "../services/productService";
import { Product } from "../types/Product";
import { Metric } from "../types/Metric";
import { ProductDTO } from "../types/ProductDTO";

interface ProductContextType {
    products: Product[];
    total: number;
    fetchProducts: (params?: Record<string, any>) => Promise<void>;
    createProduct: (product: ProductDTO) => Promise<Product>;
    updateProduct: (id: number, product: ProductDTO) => Promise<Product>;
    setProductInStock: (id: number) => Promise<Product>;
    setProductOutOfStock: (id: number) => Promise<Product>;
    deleteProduct: (id: number) => Promise<void>;
    loading: boolean;
    error: string | null;
    metrics: Metric[];
}

const ProductContext = createContext<ProductContextType | undefined>(undefined);

export const ProductProvider = ({ children }: { children: ReactNode }) => {
    const [products, setProducts] = useState<Product[]>([]);
    const [total, setTotal] = useState<number>(0);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const [metrics, setMetrics] = useState<Metric[]>([]);

    const fetchProducts = useCallback(async (params: Record<string, any> = {}) => {
        setLoading(true);
        setError(null);
        try {
            const response = await apiGetProducts(params);
            setProducts(response.content || []);
            setTotal(typeof response.totalElements === "number" ? response.totalElements : 0);
        } catch (err) {
            setError("Failed to fetch products");
            setProducts([]);
            setTotal(0);
        } finally {
            setLoading(false);
        }
    }, []);

    const fetchMetrics = useCallback(async () => {
        try {
            const data = await apiGetInventoryMetrics();
            setMetrics(data);
        } catch (err) {
            setError("Failed to fetch metrics");
            setMetrics([]);
            console.error(err);
        }
    }, []);
    const createProduct = useCallback(async (productDTO: ProductDTO): Promise<Product> => {
        setLoading(true);
        setError(null);
        try {
            const newProduct = await apiCreateProduct(productDTO);
            await fetchProducts();
            await fetchMetrics();
            return newProduct;
        } catch (err) {
            setError("Failed to create product");
            console.error(err);
            throw err;
        } finally {
            setLoading(false);
        }
    }, [fetchProducts, fetchMetrics]);

    const updateProduct = useCallback(async (id: number, productDTO: ProductDTO): Promise<Product> => {
        setLoading(true);
        setError(null);
        try {
            const updatedProduct = await apiUpdateProduct(id, productDTO);
            setProducts(prevProducts => prevProducts.map(p => p.id === id ? updatedProduct : p));
            await fetchMetrics();
            return updatedProduct;
        } catch (err) {
            setError("Failed to update product");
            console.error(err);
            throw err;
        } finally {
            setLoading(false);
        }
    }, [fetchMetrics]);

    const setProductInStock = useCallback(async (id: number): Promise<Product> => {
        setLoading(true);
        setError(null);
        try {
            const updatedProduct = await apiSetProductInStock(id);
            setProducts(prevProducts => prevProducts.map(p => p.id === id ? updatedProduct : p));
            await fetchMetrics();
            return updatedProduct;
        } catch (err) {
            setError("Failed to set product in stock");
            console.error(err);
            throw err;
        } finally {
            setLoading(false);
        }
    }, [fetchMetrics]); 

    const setProductOutOfStock = useCallback(async (id: number): Promise<Product> => {
        setLoading(true);
        setError(null);
        try {
            const updatedProduct = await apiSetProductOutOfStock(id);
            setProducts(prevProducts => prevProducts.map(p => p.id === id ? updatedProduct : p));
            await fetchMetrics();
            return updatedProduct;
        } catch (err) {
            setError("Failed to set product out of stock");
            console.error(err);
            throw err;
        } finally {
            setLoading(false);
        }
    }, [fetchMetrics]);

    const deleteProduct = useCallback(async (id: number) => {
        setLoading(true);
        setError(null);
        try {
            await apiDeleteProduct(id);
            await fetchProducts();
            await fetchMetrics();
        } catch (err) {
            setError("Failed to delete product");
            console.error(err);
            throw err;
        } finally {
            setLoading(false);
        }
    }, [fetchProducts, fetchMetrics]);
    useEffect(() => {
        fetchProducts();
        fetchMetrics();
    }, [fetchProducts, fetchMetrics]);


    return (
        <ProductContext.Provider
            value={{
                products,
                total,
                fetchProducts,
                createProduct,
                updateProduct,
                setProductInStock,
                setProductOutOfStock,
                deleteProduct,
                loading,
                error,
                metrics,
            }}
        >
            {children}
        </ProductContext.Provider>
    );
};

export const useProductContext = () => {
    const context = useContext(ProductContext);
    if (!context) throw new Error("useProductContext must be used within a ProductProvider");
    return context;
};
