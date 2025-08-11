import {
    useEffect,
    useMemo,
    useState
} from "react";

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
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";

import SaveIcon from "@mui/icons-material/Save";
import CancelIcon from "@mui/icons-material/Cancel";

import {
    useProductContext
} from "../../context/ProductContext";

import {
    useCategoryContext
} from "../../context/CategoryContext";

import {
    useDialog
} from "../../hooks/useDialog";

import {
    nonNegative,
    required
} from "../../utils/validators";

import CreateCategory from "../Category/CreateCategory";

import {
    ProductDTO
} from "../../types/ProductDTO";

export type ProductFormValues = {
    name?: string;
    categoryId?: number | "";
    stock?: number | "";
    price?: number | "";
    expirationDate?: string;
};

type Props = {
    open: boolean;
    title: string;
    initial?: ProductFormValues;
    onSubmit: (dto: ProductDTO) => Promise<void> | void;
    onClose: () => void;
    successMessage?: string;
};

const ProductForm = ({ open, title, initial, onSubmit, onClose, successMessage = "Saved successfully!" }: Props) => {
    const { fetchProducts } = useProductContext();
    const { categories, fetchCategories } = useCategoryContext();

    const defaultCategoryId = useMemo(() => (categories.length > 0 ? categories[0].id : "" as const), [categories]);

    const [name, setName] = useState(initial?.name ?? "");
    const [categoryId, setCategoryId] = useState<number | "">(initial?.categoryId ?? defaultCategoryId);
    const [stock, setStock] = useState<number | "">(initial?.stock ?? "");
    const [price, setPrice] = useState<number | "">(initial?.price ?? "");
    const [expirationDate, setExpirationDate] = useState<string>(initial?.expirationDate ?? "");

    const [formError, setFormError] = useState<string | null>(null);
    const [successAlert, setSuccessAlert] = useState(false);
    const [touched, setTouched] = useState<{ [key: string]: boolean }>({});
    const { open: createCategoryOpen, openDialog: openCreateCategory, closeDialog: closeCreateCategory } = useDialog(false);

    useEffect(() => {
        if (open) {
            setName(initial?.name ?? "");
            setCategoryId(initial?.categoryId ?? defaultCategoryId);
            setStock(initial?.stock ?? "");
            setPrice(initial?.price ?? "");
            setExpirationDate(initial?.expirationDate ?? "");
            setFormError(null);
            setTouched({});
        }
    }, [open, initial, defaultCategoryId]);

    useEffect(() => {
        if (categoryId && typeof categoryId === "number" && !categories.some((c) => c.id === categoryId)) {
            setCategoryId("");
        }
    }, [categories, categoryId]);

    const handleSave = async () => {
        const errors = {
            name: required(name),
            categoryId: required(categoryId),
            stock: nonNegative(stock),
            price: nonNegative(price),
        } as const;
        setTouched({ name: true, categoryId: true, stock: true, price: true });

        if (Object.values(errors).some((msg) => msg)) {
            setFormError("Please fix the highlighted fields.");
            return;
        }
        setFormError(null);

        const dto: ProductDTO = {
            name: name!,
            categoryId: Number(categoryId),
            stock: Number(stock),
            price: Number(price),
            expirationDate: expirationDate || null,
        };
        await onSubmit(dto);
        await fetchProducts();
        setSuccessAlert(true);
        handleCancel();
    };

    const handleCancel = () => {
        setName("");
        setCategoryId("");
        setStock("");
        setPrice("");
        setExpirationDate("");
        setFormError(null);
        setTouched({});
        onClose();
    };

    const handleCategoryChange = (e: any) => {
        const value = e.target.value;
        if (value === "__create__") {
            openCreateCategory();
        } else {
            setCategoryId(value);
            setTouched((t) => ({ ...t, categoryId: true }));
        }
    };

    const handleCategoryCreated = async (newCatId: number) => {
        await fetchProducts();
        await fetchCategories();
        setCategoryId(newCatId);
        closeCreateCategory();
    };

    return (
        <>
            <Dialog open={open} onClose={handleCancel} maxWidth="sm" fullWidth>
                <DialogTitle>{title}</DialogTitle>
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
                            onChange={(e) => setName(e.target.value)}
                            onBlur={() => setTouched((t) => ({ ...t, name: true }))}
                            error={touched.name && !!required(name)}
                            helperText={touched.name ? required(name) : ""}
                            fullWidth
                        />
                        <FormControl fullWidth error={touched.categoryId && !!required(categoryId)}>
                            <InputLabel id="category-label">
                                Category <span style={{ color: "red" }}>*</span>
                            </InputLabel>
                            <Select
                                labelId="category-label"
                                value={categoryId}
                                onChange={handleCategoryChange}
                                onBlur={() => setTouched((t) => ({ ...t, categoryId: true }))}
                                input={<OutlinedInput label="Category" />}
                            >
                                <MenuItem value="">
                                    <em>Select a category</em>
                                </MenuItem>
                                {categories.map((cat) => (
                                    <MenuItem key={cat.id} value={cat.id}>
                                        {cat.name}
                                    </MenuItem>
                                ))}
                                <MenuItem value="__create__" sx={{ fontStyle: "italic", color: "primary.main" }}>
                                    + Create new category
                                </MenuItem>
                            </Select>
                            {touched.categoryId && required(categoryId) && (
                                <Box sx={{ color: "error.main", fontSize: 12, mt: 0.5, ml: 2 }}>{required(categoryId)}</Box>
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
                            onChange={(e) => setStock(Number(e.target.value))}
                            onBlur={() => setTouched((t) => ({ ...t, stock: true }))}
                            error={touched.stock && !!nonNegative(stock)}
                            helperText={touched.stock ? nonNegative(stock) : ""}
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
                            onChange={(e) => setPrice(Number(e.target.value))}
                            onBlur={() => setTouched((t) => ({ ...t, price: true }))}
                            error={touched.price && !!nonNegative(price)}
                            helperText={touched.price ? nonNegative(price) : ""}
                            fullWidth
                        />
                        <TextField
                            label="Expiration Date"
                            type="date"
                            value={expirationDate}
                            onChange={(e) => setExpirationDate(e.target.value)}
                            slotProps={{ inputLabel: { shrink: true } }}
                            fullWidth
                        />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button variant="contained" onClick={handleSave} startIcon={<SaveIcon />}>Save</Button>
                    <Button variant="outlined" onClick={handleCancel} startIcon={<CancelIcon />}>Cancel</Button>
                </DialogActions>
            </Dialog>
            <Snackbar
                open={successAlert}
                autoHideDuration={2500}
                onClose={() => setSuccessAlert(false)}
                anchorOrigin={{ vertical: "top", horizontal: "center" }}
            >
                <Alert onClose={() => setSuccessAlert(false)} severity="success" sx={{ width: "100%" }}>
                    {successMessage}
                </Alert>
            </Snackbar>
            <CreateCategory open={createCategoryOpen} onClose={closeCreateCategory} onCreated={handleCategoryCreated} />
        </>
    );
};

export default ProductForm;
