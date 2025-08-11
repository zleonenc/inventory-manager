import {
    useProductContext
} from "../../context/ProductContext";
import ProductForm from "./ProductForm";

const CreateProduct = ({ open, onClose }: { open: boolean; onClose: () => void }) => {
    const { createProduct } = useProductContext();

    return (
        <ProductForm
            open={open}
            title="Create Product"
            onSubmit={async (dto) => { await createProduct(dto); }}
            onClose={onClose}
            successMessage="Product created successfully!"
        />
    );
};

export default CreateProduct;
