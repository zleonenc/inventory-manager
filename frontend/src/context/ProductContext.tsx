import { createContext, useContext, useEffect, useState, ReactNode } from "react";

import { getProducts, getInventoryMetrics, createProduct as apiCreateProduct, updateProduct as apiUpdateProduct, setProductInStock as apiSetProductInStock, setProductOutOfStock as apiSetProductOutOfStock, deleteProduct as apiDeleteProduct } from "../services/productService";
import { Product } from "../types/Product";
import { Metric } from "../types/Metric";
import { ProductDTO } from "../types/ProductDTO";

interface ProductContextType {
    products: Product[];
    total: number;
    fetchProducts: (params?: Record<string, any>) => Promise<void>;
    createProduct: (product: ProductDTO) => Promise<void>;
    updateProduct: (id: number, product: ProductDTO) => Promise<void>;
    setProductInStock: (id: number) => Promise<void>;
    setProductOutOfStock: (id: number) => Promise<void>;
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

    const fetchProducts = async (params: Record<string, any> = {}) => {
        setLoading(true);
        setError(null);
        try {
            const response = await getProducts(params);
            setProducts(response.content || []);
            setTotal(typeof response.totalElements === "number" ? response.totalElements : 0);
        } catch (err) {
            setError("Failed to fetch products");
            setProducts([]);
            setTotal(0);
        } finally {
            setLoading(false);
        }
    };

    const createProduct = async (product: ProductDTO) => {
        setLoading(true);
        setError(null);
        try {
            await apiCreateProduct(product);
            await fetchProducts();
        } catch (err) {
            setError("Failed to create product");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const updateProduct = async (id: number, product: ProductDTO) => {
        setLoading(true);
        setError(null);
        try {
            await apiUpdateProduct(id, product);
            await fetchProducts();
        } catch (err) {
            setError("Failed to update product");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const setProductInStock = async (id: number) => {
        setLoading(true);
        setError(null);
        try {
            await apiSetProductInStock(id);
            await fetchProducts();
        } catch (err) {
            setError("Failed to set product in stock");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const setProductOutOfStock = async (id: number) => {
        setLoading(true);
        setError(null);
        try {
            await apiSetProductOutOfStock(id);
            await fetchProducts();
        } catch (err) {
            setError("Failed to set product out of stock");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const deleteProduct = async (id: number) => {
        setLoading(true);
        setError(null);
        try {
            await apiDeleteProduct(id);
            await fetchProducts();
        } catch (err) {
            setError("Failed to delete product");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const fetchMetrics = async () => {
        try {
            const data = await getInventoryMetrics();
            setMetrics(data);
        } catch (err) {
            setMetrics([]);
        }
    };

    useEffect(() => {
        fetchProducts();
        fetchMetrics();
    }, []);

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

