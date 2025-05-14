import { useProductContext } from "../context/ProductContext";

const ProductList = () => {
    const {products} = useProductContext();

    return (
        <div>
            <h1>
                <ul>
                    {products.map(p => (
                        <li key={p.id}>
                            {p.name} - {p.stock} - {p.active} - {p.category.name}
                        </li>
                    ))}
                </ul>
            </h1>
        </div>
    )
}

export default ProductList;