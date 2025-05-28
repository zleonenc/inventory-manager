import { useState } from "react";

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

const CreateCategory = ({ open, onClose, onCreated }: { open: boolean; onClose: () => void; onCreated?: (id: number) => void }) => {
    const { addCategory, categories } = useCategoryContext();
    const [name, setName] = useState("");
    const [formError, setFormError] = useState<string | null>(null);
    const [touched, setTouched] = useState<{ name: boolean }>({ name: false });
    const [successAlert, setSuccessAlert] = useState(false);

    const handleSave = async () => {
        setTouched({ name: true });

        // Check for duplicate name (case-insensitive, trimmed)
        const exists = categories.some(
            (cat) => cat.name.trim().toLowerCase() === name.trim().toLowerCase()
        );
        if (exists) {
            setFormError("A category with this name already exists.");
            return;
        }
        if (!name.trim()) {
            setFormError("Name is required.");
            return;
        }
        setFormError(null);
        const newCat = await addCategory({ name: name.trim() });
        setSuccessAlert(true);
        if (onCreated && newCat && newCat.id) {
            onCreated(newCat.id);
        }
        handleCancel();
    };

    const handleCancel = () => {
        setName("");
        setFormError(null);
        setTouched({ name: false });
        onClose();
    };

    const handleAlertClose = () => {
        setSuccessAlert(false);
        handleCancel();
    };

    return (
        <>
            <Dialog open={open} onClose={handleCancel} maxWidth="xs" fullWidth>
                <DialogTitle>Create Category</DialogTitle>
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
                        onBlur={() => setTouched({ name: true })}
                        error={touched.name && !name}
                        helperText={touched.name && !name ? "Name is required" : ""}
                        fullWidth
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
            </Dialog>
            <Snackbar
                open={successAlert}
                autoHideDuration={3000}
                onClose={handleAlertClose}
                anchorOrigin={{ vertical: "top", horizontal: "center" }}
            >
                <Alert onClose={handleAlertClose} severity="success" sx={{ width: '100%' }}>
                    Category created successfully!
                </Alert>
            </Snackbar>
        </>
    );
};

export default CreateCategory;
