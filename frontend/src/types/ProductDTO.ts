export interface ProductDTO {
    name: string;
    categoryId: number;
    stock: number;
    price: number;
    expirationDate: string | null;
}