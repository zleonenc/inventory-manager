import { createContext, useContext, useEffect, useState, ReactNode } from "react";
import { getCategories, createCategory, updateCategory, deleteCategory } from "../services/categoryService";
import { Category } from "../types/Category";
import { CategoryDTO } from "../types/CategoryDTO";

interface CategoryContextType {
  categories: Category[];
  loading: boolean;
  error: string | null;
  fetchCategories: () => void;
  addCategory: (category: CategoryDTO) => Promise<void>;
  editCategory: (id: number, category: CategoryDTO) => Promise<void>;
  removeCategory: (id: number) => Promise<void>;
}

const CategoryContext = createContext<CategoryContextType | undefined>(undefined);

export const CategoryProvider = ({ children }: { children: ReactNode }) => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const fetchCategories = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getCategories();
      setCategories(data);
    } catch (err) {
      setError("Failed to fetch categories");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

const addCategory = async (categoryDTO: CategoryDTO) => {
    setLoading(true);
    setError(null);
    try {
        await createCategory(categoryDTO);
        await fetchCategories();
    } catch (err) {
        setError("Failed to add category");
        console.error(err);
    } finally {
        setLoading(false);
    }
};

const editCategory = async (id: number, categoryDTO: CategoryDTO) => {
    setLoading(true);
    setError(null);
    try {
        await updateCategory(id, categoryDTO);
        await fetchCategories();
    } catch (err) {
        setError("Failed to update category");
        console.error(err);
    } finally {
        setLoading(false);
    }
};

  const removeCategory = async (id: number) => {
    setLoading(true);
    setError(null);
    try {
      await deleteCategory(id);
      await fetchCategories();
    } catch (err) {
      setError("Failed to delete category");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  return (
    <CategoryContext.Provider
      value={{
        categories,
        loading,
        error,
        fetchCategories,
        addCategory,
        editCategory,
        removeCategory,
      }}
    >
      {children}
    </CategoryContext.Provider>
  );
};

export const useCategoryContext = () => {
  const context = useContext(CategoryContext);
  if (!context) throw new Error("useCategoryContext must be used within a CategoryProvider");
  return context;
};