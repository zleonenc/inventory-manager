import {
    createContext,
    useContext,
    useEffect,
    useState,
    ReactNode,
    useCallback,
    useMemo
} from "react";

import {
    getProducts as apiGetProducts,
    getInventoryMetrics as apiGetInventoryMetrics,
    createProduct as apiCreateProduct,
    updateProduct as apiUpdateProduct,
    setProductInStock as apiSetProductInStock,
    setProductOutOfStock as apiSetProductOutOfStock,
    deleteProduct as apiDeleteProduct
} from "../services/productService";
import {
    useAsync
} from "../hooks/useAsync";

import {
    Product
} from "../types/Product";
import {
    Metric
} from "../types/Metric";
import {
    ProductDTO
} from "../types/ProductDTO";


type ProductFilters = {
    name?: string;
    categories?: number[];
    available?: string;
};

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
    lastFilters: ProductFilters;
    setLastFilters: (filters: ProductFilters) => void;
}

const ProductContext = createContext<ProductContextType | undefined>(undefined);

export const ProductProvider = ({ children }: { children: ReactNode }) => {
    const [products, setProducts] = useState<Product[]>([]);
    const [total, setTotal] = useState<number>(0);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const [metrics, setMetrics] = useState<Metric[]>([]);
    const [lastFilters, setLastFilters] = useState<ProductFilters>({});

    const fetchProducts = useCallback(async (params: Record<string, any> = {}) => {
        // Merge provided params over saved filters by default
        const effectiveParams = Object.keys(params).length ? params : { ...lastFilters };
        setLoading(true);
        setError(null);
        try {
            const response = await apiGetProducts(effectiveParams);
            setProducts(response.content || []);
            setTotal(typeof response.totalElements === "number" ? response.totalElements : 0);
        } catch (err) {
            setError("Failed to fetch products");
            setProducts([]);
            setTotal(0);
            throw err;
        } finally {
            setLoading(false);
        }
    }, [lastFilters]);

    const fetchMetrics = useCallback(async () => {
        try {
            const data = await apiGetInventoryMetrics();
            setMetrics(data);
        } catch (err) {
            setError("Failed to fetch metrics");
            setMetrics([]);
            console.error(err);
            throw err;
        }
    }, []);

    const createProductFn = useCallback(async (productDTO: ProductDTO): Promise<Product> => {
        const newProduct = await apiCreateProduct(productDTO);
        // Refresh using current filters
        await fetchProducts();
        await fetchMetrics();
        return newProduct;
    }, [fetchProducts, fetchMetrics]);
    const { run: createProduct } = useAsync(createProductFn);

    const updateProductFn = useCallback(async (id: number, productDTO: ProductDTO): Promise<Product> => {
        const updatedProduct = await apiUpdateProduct(id, productDTO);
        setProducts(prevProducts => prevProducts.map(p => p.id === id ? updatedProduct : p));
        await fetchMetrics();
        return updatedProduct;
    }, [fetchMetrics]);
    const { run: updateProduct } = useAsync(updateProductFn);

    const setProductInStockFn = useCallback(async (id: number): Promise<Product> => {
        const updatedProduct = await apiSetProductInStock(id);
        setProducts(prevProducts => prevProducts.map(p => p.id === id ? updatedProduct : p));
        await fetchMetrics();
        return updatedProduct;
    }, [fetchMetrics]);
    const { run: setProductInStock } = useAsync(setProductInStockFn);

    const setProductOutOfStockFn = useCallback(async (id: number): Promise<Product> => {
        const updatedProduct = await apiSetProductOutOfStock(id);
        setProducts(prevProducts => prevProducts.map(p => p.id === id ? updatedProduct : p));
        await fetchMetrics();
        return updatedProduct;
    }, [fetchMetrics]);
    const { run: setProductOutOfStock } = useAsync(setProductOutOfStockFn);

    const deleteProductFn = useCallback(async (id: number) => {
        await apiDeleteProduct(id);
        // Do not refetch products here to allow callers to include current pagination/sort/filters.
        await fetchMetrics();
    }, [fetchMetrics]);
    const { run: deleteProduct } = useAsync(deleteProductFn);
    useEffect(() => {
        (async () => {
            setLoading(true);
            setError(null);
            try {
                await fetchMetrics();
            } catch (err) {
                // errors already set in respective calls
            } finally {
                setLoading(false);
            }
        })();
    }, [fetchMetrics]);


    const contextValue = useMemo(
        () => ({
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
            lastFilters,
            setLastFilters,
        }), [
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
        lastFilters,
        setLastFilters,
    ]
    );

    return (
        <ProductContext.Provider value={contextValue}>
            {children}
        </ProductContext.Provider>
    );
};

export const useProductContext = () => {
    const context = useContext(ProductContext);
    if (!context) throw new Error("useProductContext must be used within a ProductProvider");
    return context;
};
