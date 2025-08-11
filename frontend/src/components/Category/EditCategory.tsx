import {
    useCallback
} from "react";

import {
    useCategoryContext
} from "../../context/CategoryContext";

import CategoryForm from "./CategoryForm";

import {
    Category
} from "../../types/Category";

interface EditCategoryProps {
    open: boolean;
    onClose: () => void;
    category: Category | null;
}

const EditCategory = ({ open, onClose, category }: EditCategoryProps) => {
    const { editCategory } = useCategoryContext();
    const name = category?.name ?? "";
    const id = category?.id ?? null;

    const onSubmit = useCallback((newName: string) => {
        if (!id) return Promise.resolve();
        return editCategory(id, { name: newName });
    }, [editCategory, id]);

    return (
        <CategoryForm
            open={open}
            title="Edit Category"
            initialName={name}
            existingId={id ?? null}
            onSubmit={onSubmit}
            onClose={onClose}
            successMessage="Category updated successfully!"
        />
    );
};

export default EditCategory;