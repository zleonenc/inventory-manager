import {
    useMemo
} from "react";

import {
    useProductContext
} from "../../context/ProductContext";

import ProductForm from "./ProductForm";

import {
    Product
} from "../../types/Product";

interface EditProductProps {
    open: boolean;
    onClose: () => void;
    product: Product | null;
}

const EditProduct = ({ open, onClose, product }: EditProductProps) => {
    const { updateProduct } = useProductContext();

    const initial = useMemo(() => ({
        name: product?.name ?? "",
        categoryId: (product?.category?.id ?? ("" as const)),
        stock: (product?.stock ?? ("" as const)),
        price: (product?.price ?? ("" as const)),
        expirationDate: product?.expirationDate ? product!.expirationDate.substring(0, 10) : "",
    }), [product]);

    return (
        <ProductForm
            open={open}
            title="Edit Product"
            initial={initial}
            onSubmit={async (dto) => { if (product) { await updateProduct(product.id, dto); } }}
            onClose={onClose}
            successMessage="Product updated successfully!"
        />
    );
};

export default EditProduct;