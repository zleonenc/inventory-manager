import { useState, useEffect } from "react";

import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import Alert from "@mui/material/Alert";
import Snackbar from "@mui/material/Snackbar";
import CancelIcon from "@mui/icons-material/Cancel";
import SaveIcon from "@mui/icons-material/Save";

import { useCategoryContext } from "../../context/CategoryContext";
import { Category } from "../../types/Category";

interface EditCategoryProps {
    open: boolean;
    onClose: () => void;
    category: Category | null;
}

const EditCategory = ({ open, onClose, category }: EditCategoryProps) => {
    const { editCategory, categories } = useCategoryContext();
    const [name, setName] = useState("");
    const [formError, setFormError] = useState<string | null>(null);
    const [touched, setTouched] = useState(false);
    const [successAlert, setSuccessAlert] = useState(false);

    useEffect(() => {
        if (category) {
            setName(category.name || "");
            setFormError(null);
            setTouched(false);
        }
    }, [category, open]);

    const handleSave = async () => {
        setTouched(true);
        if (!name.trim() || !category) {
            setFormError("Category name is required.");
            return;
        }
        // Check for duplicate name (case-insensitive, trimmed), excluding the current category
        const exists = categories.some(
            (cat) =>
                cat.id !== category.id &&
                cat.name.trim().toLowerCase() === name.trim().toLowerCase()
        );
        if (exists) {
            setFormError("A category with this name already exists.");
            return;
        }
        setFormError(null);
        await editCategory(category.id, { name: name.trim() });
        setSuccessAlert(true);
        handleCancel();
    };

    const handleCancel = () => {
        setFormError(null);
        setTouched(false);
        onClose();
    };

    return (
        <>
            <Dialog open={open} onClose={handleCancel} maxWidth="xs" fullWidth>
                <DialogTitle>Edit Category</DialogTitle>
                <DialogContent>
                    {formError && <Alert severity="warning" sx={{ mb: 2 }}>{formError}</Alert>}
                    <TextField
                        label={
                            <span>
                                Name <span style={{ color: "red" }}>*</span>
                            </span>
                        }
                        value={name}
                        onChange={e => setName(e.target.value)}
                        onBlur={() => setTouched(true)}
                        error={touched && !name.trim()}
                        helperText={touched && !name.trim() ? "Name is required" : ""}
                        fullWidth
                        autoFocus
                        sx={{ mt: 2 }}
                    />
                </DialogContent>
                <DialogActions>
                    <Button
                        variant="contained"
                        onClick={handleSave}
                        startIcon={<SaveIcon />}
                    >Save</Button>
                    <Button
                        variant="outlined"
                        onClick={handleCancel}
                        startIcon={<CancelIcon />}
                    >Cancel</Button>
                </DialogActions>
                <Snackbar
                    open={successAlert}
                    autoHideDuration={3000}
                    onClose={() => setSuccessAlert(false)}
                    anchorOrigin={{ vertical: "top", horizontal: "center" }}
                >
                    <Alert onClose={() => setSuccessAlert(false)} severity="success">
                        Category updated successfully!
                    </Alert>
                </Snackbar>
            </Dialog>
        </>
    );
};

export default EditCategory;