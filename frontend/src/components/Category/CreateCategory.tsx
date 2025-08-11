import {
    useCategoryContext
} from "../../context/CategoryContext";

import CategoryForm from "./CategoryForm";

const CreateCategory = ({ open, onClose, onCreated }: { open: boolean; onClose: () => void; onCreated?: (id: number) => void }) => {
    const { addCategory } = useCategoryContext();

    return (
        <CategoryForm
            open={open}
            title="Create Category"
            onSubmit={(name) => addCategory({ name })}
            onClose={onClose}
            onCreatedId={(id) => onCreated?.(id)}
            successMessage="Category created successfully!"
        />
    );
};

export default CreateCategory;
