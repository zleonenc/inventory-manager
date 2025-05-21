import { useProductContext } from "../../context/ProductContext";
import { useCategoryContext } from "../../context/CategoryContext";
import { useState, useEffect } from "react";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import InputLabel from "@mui/material/InputLabel";
import FormControl from "@mui/material/FormControl";
import Select from "@mui/material/Select";
import Button from "@mui/material/Button";
import MenuItem from "@mui/material/MenuItem";
import OutlinedInput from "@mui/material/OutlinedInput";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import Alert from "@mui/material/Alert";
import { Product } from "../../types/Product";
import { ProductDTO } from "../../types/ProductDTO";

interface EditProductProps {
  open: boolean;
  onClose: () => void;
  product: Product | null;
}

const EditProduct = ({ open, onClose, product }: EditProductProps) => {
  const { updateProduct, fetchProducts } = useProductContext();
  const { categories } = useCategoryContext();

  const [name, setName] = useState("");
  const [categoryId, setCategoryId] = useState<number | "">("");
  const [stock, setStock] = useState<number | "">("");
  const [price, setPrice] = useState<number | "">("");
  const [expirationDate, setExpirationDate] = useState<string>("");
  const [formError, setFormError] = useState<string | null>(null);
  const [touched, setTouched] = useState<{ [key: string]: boolean }>({});

  // Populate form fields when product changes
  useEffect(() => {
    if (product) {
      setName(product.name || "");
      setCategoryId(product.category?.id ?? "");
      setStock(product.stock ?? "");
      setPrice(product.price ?? "");
      setExpirationDate(product.expirationDate ? product.expirationDate.substring(0, 10) : "");
      setFormError(null);
      setTouched({});
    }
  }, [product, open]);

  useEffect(() => {
    if (categoryId && !categories.some(cat => cat.id === categoryId)) {
      setCategoryId("");
    }
  }, [categories, categoryId]);

  const handleSave = async () => {
    const errors = {
      name: !name,
      categoryId: !categoryId,
      stock: stock === "" || isNaN(Number(stock)),
      price: price === "" || isNaN(Number(price)),
    };
    setTouched({
      name: true,
      categoryId: true,
      stock: true,
      price: true,
    });

    if (Object.values(errors).some(Boolean)) {
      setFormError("Please fill in all required fields.");
      return;
    }
    setFormError(null);

    await updateProduct(product!.id, {
      name,
      categoryId: Number(categoryId),
      stock: Number(stock),
      price: Number(price),
      expirationDate: expirationDate || null,
    } as ProductDTO);
    fetchProducts();
    handleCancel();
  };

  const handleCancel = () => {
    setFormError(null);
    setTouched({});
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleCancel} maxWidth="sm" fullWidth>
      <DialogTitle>Edit Product</DialogTitle>
      <DialogContent>
        {formError && (
          <Alert severity="warning" sx={{ mb: 2 }}>
            {formError}
          </Alert>
        )}
        <Box display="flex" flexDirection="column" gap={3} mt={1}>
          <TextField
            label={
              <span>
                Name <span style={{ color: "red" }}>*</span>
              </span>
            }
            value={name}
            onChange={e => setName(e.target.value)}
            onBlur={() => setTouched(t => ({ ...t, name: true }))}
            error={touched.name && !name}
            helperText={touched.name && !name ? "Name is required" : ""}
            fullWidth
          />
          <FormControl
            fullWidth
            error={touched.categoryId && !categoryId}
          >
            <InputLabel id="category-label">
              Category <span style={{ color: "red" }}>*</span>
            </InputLabel>
            <Select
              labelId="category-label"
              value={categoryId}
              onChange={e => setCategoryId(e.target.value as number)}
              onBlur={() => setTouched(t => ({ ...t, categoryId: true }))}
              input={<OutlinedInput label="Category" />}
            >
              <MenuItem value="">
                <em>Select a category</em>
              </MenuItem>
              {categories.map(cat => (
                <MenuItem key={cat.id} value={cat.id}>
                  {cat.name}
                </MenuItem>
              ))}
            </Select>
            {touched.categoryId && !categoryId && (
              <Box sx={{ color: "error.main", fontSize: 12, mt: 0.5, ml: 2 }}>
                Category is required
              </Box>
            )}
          </FormControl>
          <TextField
            label={
              <span>
                Stock <span style={{ color: "red" }}>*</span>
              </span>
            }
            type="number"
            value={stock}
            onChange={e => setStock(Number(e.target.value))}
            onBlur={() => setTouched(t => ({ ...t, stock: true }))}
            error={touched.stock && (stock === "" || isNaN(Number(stock)))}
            helperText={
              touched.stock && (stock === "" || isNaN(Number(stock)))
                ? "Stock is required and must be a number"
                : ""
            }
            fullWidth
          />
          <TextField
            label={
              <span>
                Unit Price <span style={{ color: "red" }}>*</span>
              </span>
            }
            type="number"
            value={price}
            onChange={e => setPrice(Number(e.target.value))}
            onBlur={() => setTouched(t => ({ ...t, price: true }))}
            error={touched.price && (price === "" || isNaN(Number(price)))}
            helperText={
              touched.price && (price === "" || isNaN(Number(price)))
                ? "Unit price is required and must be a number"
                : ""
            }
            fullWidth
          />
          <TextField
            label="Expiration Date"
            type="date"
            value={expirationDate}
            onChange={e => setExpirationDate(e.target.value)}
            InputLabelProps={{ shrink: true }}
            fullWidth
          />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button variant="contained" onClick={handleSave}>Save</Button>
        <Button variant="outlined" onClick={handleCancel}>Cancel</Button>
      </DialogActions>
    </Dialog>
  );
};

export default EditProduct;