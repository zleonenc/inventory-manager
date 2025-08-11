import {
    createContext,
    useContext,
    useEffect,
    useMemo,
    useState,
    ReactNode,
    useCallback
} from "react";

import {
    getCategories,
    createCategory,
    updateCategory, deleteCategory
} from "../services/categoryService";

import {
    useAsync
} from "../hooks/useAsync";

import {
    Category
} from "../types/Category";
import {
    CategoryDTO
} from "../types/CategoryDTO";

interface CategoryContextType {
    categories: Category[];
    loading: boolean;
    error: string | null;
    fetchCategories: () => void;
    addCategory: (category: CategoryDTO) => Promise<Category>;
    editCategory: (id: number, category: CategoryDTO) => Promise<void>;
    removeCategory: (id: number) => Promise<void>;
}

const CategoryContext = createContext<CategoryContextType | undefined>(undefined);

export const CategoryProvider = ({ children }: { children: ReactNode }) => {
    const [categories, setCategories] = useState<Category[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    const fetchCategoriesFn = useCallback(async () => {
        const data = await getCategories();
        setCategories(data);
    }, []);
    const fetchCategoriesAsync = useAsync(fetchCategoriesFn);
    const fetchCategories = fetchCategoriesAsync.run;

    const addCategoryFn = useCallback(async (categoryDTO: CategoryDTO): Promise<Category> => {
        const newCategory = await createCategory(categoryDTO);
        await fetchCategories();
        return newCategory;
    }, [fetchCategories]);
    const addCategoryAsync = useAsync(addCategoryFn);
    const addCategory = addCategoryAsync.run;

    const editCategoryFn = useCallback(async (id: number, categoryDTO: CategoryDTO) => {
        await updateCategory(id, categoryDTO);
        await fetchCategories();
    }, [fetchCategories]);
    const editCategoryAsync = useAsync(editCategoryFn);
    const editCategory = editCategoryAsync.run;

    const removeCategoryFn = useCallback(async (id: number) => {
        await deleteCategory(id);
        await fetchCategories();
    }, [fetchCategories]);
    const removeCategoryAsync = useAsync(removeCategoryFn);
    const removeCategory = removeCategoryAsync.run;

    useEffect(() => {
        (async () => {
            setLoading(true);
            setError(null);
            try {
                await fetchCategories();
            } catch (err) {
                setError("Failed to fetch categories");
                console.error(err);
            } finally {
                setLoading(false);
            }
        })();
    }, [fetchCategories]);

    // unify loading/error from async hooks
    const derivedLoading =
        fetchCategoriesAsync.loading || addCategoryAsync.loading || editCategoryAsync.loading || removeCategoryAsync.loading;
    const derivedError =
        fetchCategoriesAsync.error || addCategoryAsync.error || editCategoryAsync.error || removeCategoryAsync.error;
    const effectiveLoading = loading || derivedLoading;
    const effectiveError = error || derivedError;

    const contextValue = useMemo(
        () => ({
            categories,
            loading: effectiveLoading,
            error: effectiveError,
            fetchCategories,
            addCategory,
            editCategory,
            removeCategory,
        }), [
        categories,
        effectiveLoading,
        effectiveError,
        fetchCategories,
        addCategory,
        editCategory,
        removeCategory,
    ]
    );

    return (
        <CategoryContext.Provider value={contextValue}>
            {children}
        </CategoryContext.Provider>
    );
};

export const useCategoryContext = () => {
    const context = useContext(CategoryContext);
    if (!context) throw new Error("useCategoryContext must be used within a CategoryProvider");
    return context;
};