import {
    useEffect,
    useState
} from "react";

import Box from "@mui/material/Box";
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

import {
    useCategoryContext
} from "../../context/CategoryContext";
import {
    required
} from "../../utils/validators";

type Props = {
    open: boolean;
    title: string;
    initialName?: string;
    existingId?: number | null;
    successMessage?: string;
    onSubmit: (name: string) => Promise<{ id?: number } | void>;
    onClose: () => void;
    onCreatedId?: (id: number) => void; // optional: notify parent with new id
};

const CategoryForm = ({
    open,
    title,
    initialName = "",
    existingId = null,
    successMessage = "Saved successfully!",
    onSubmit,
    onClose,
    onCreatedId,
}: Props) => {
    const { categories } = useCategoryContext();
    const [name, setName] = useState<string>(initialName);
    const [formError, setFormError] = useState<string | null>(null);
    const [touched, setTouched] = useState<{ name: boolean }>({ name: false });
    const [successAlert, setSuccessAlert] = useState(false);

    useEffect(() => {
        if (open) {
            setName(initialName ?? "");
            setFormError(null);
            setTouched({ name: false });
        }
    }, [open, initialName]);

    const handleSave = async () => {
        setTouched({ name: true });
        const trimmed = name.trim();
        if (required(trimmed)) {
            setFormError("Name is required.");
            return;
        }

        const exists = categories.some(
            (cat) =>
                cat.name.trim().toLowerCase() === trimmed.toLowerCase() &&
                (existingId == null || cat.id !== existingId)
        );
        if (exists) {
            setFormError("A category with this name already exists.");
            return;
        }

        setFormError(null);
        const res = (await onSubmit(trimmed)) || {};
        if (res && typeof (res as any).id === "number" && onCreatedId) {
            onCreatedId((res as any).id);
        }
        setSuccessAlert(true);
        handleCancel();
    };

    const handleCancel = () => {
        setFormError(null);
        setTouched({ name: false });
        onClose();
    };

    return (
        <>
            <Dialog open={open} onClose={handleCancel} maxWidth="xs" fullWidth>
                <DialogTitle>{title}</DialogTitle>
                <DialogContent>
                    {formError && <Alert severity="warning" sx={{ mb: 2 }}>{formError}</Alert>}
                    <Box display="flex" flexDirection="column" gap={3} mt={1}>
                        <TextField
                            label={
                                <span>
                                    Name <span style={{ color: "red" }}>*</span>
                                </span>
                            }
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            onBlur={() => setTouched({ name: true })}
                            error={touched.name && !!required(name.trim())}
                            helperText={touched.name ? required(name.trim()) : ""}
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
                autoHideDuration={3000}
                onClose={() => setSuccessAlert(false)}
                anchorOrigin={{ vertical: "top", horizontal: "center" }}
            >
                <Alert onClose={() => setSuccessAlert(false)} severity="success" sx={{ width: "100%" }}>
                    {successMessage}
                </Alert>
            </Snackbar>
        </>
    );
};

export default CategoryForm;
