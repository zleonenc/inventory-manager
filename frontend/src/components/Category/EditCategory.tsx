import { useState, useEffect } from "react";
import { useCategoryContext } from "../../context/CategoryContext";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import Alert from "@mui/material/Alert";
import { Category } from "../../types/Category";

interface EditCategoryProps {
  open: boolean;
  onClose: () => void;
  category: Category | null;
}

const EditCategory = ({ open, onClose, category }: EditCategoryProps) => {
  const { editCategory } = useCategoryContext();
  const [name, setName] = useState("");
  const [formError, setFormError] = useState<string | null>(null);
  const [touched, setTouched] = useState(false);

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
    setFormError(null);
    await editCategory(category.id, { name: name.trim() });
    handleCancel();
  };

  const handleCancel = () => {
    setFormError(null);
    setTouched(false);
    onClose();
  };

  return (
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
        <Button variant="contained" onClick={handleSave}>Save</Button>
        <Button variant="outlined" onClick={handleCancel}>Cancel</Button>
      </DialogActions>
    </Dialog>
  );
};

export default EditCategory;