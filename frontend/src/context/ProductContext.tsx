import { createContext, useContext, useEffect, useState, ReactNode } from "react";
import { getProducts, getInventoryMetrics, ProductPagedResponse } from "../services/productService";
import { Product } from "../types/Product";
import { Metric } from "../types/Metric";

interface ProductContextType {
  products: Product[];
  total: number;
  fetchProducts: (params?: Record<string, any>) => Promise<void>;
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

