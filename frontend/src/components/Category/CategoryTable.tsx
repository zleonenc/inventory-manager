import { useCategoryContext } from "../../context/CategoryContext";
import { useState } from "react";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";

const CategoryTable = () => {
    const { categories, editCategory, removeCategory } = useCategoryContext();
    const [editOpen, setEditOpen] = useState(false);
    const [deleteOpen, setDeleteOpen] = useState(false);
    const [selectedCategory, setSelectedCategory] = useState<{ id: number; name: string } | null>(null);
    const [editName, setEditName] = useState("");

    // Open edit dialog
    const handleEdit = (category: { id: number; name: string }) => {
        setSelectedCategory(category);
        setEditName(category.name);
        setEditOpen(true);
    };

    // Open delete dialog
    const handleDelete = (category: { id: number; name: string }) => {
        setSelectedCategory(category);
        setDeleteOpen(true);
    };

    // Confirm edit
    const handleEditSave = async () => {
        if (selectedCategory && editName.trim()) {
            await editCategory(selectedCategory.id, { name: editName.trim() });
        }
        setEditOpen(false);
        setSelectedCategory(null);
    };

    // Confirm delete
    const handleDeleteConfirm = async () => {
        if (selectedCategory) {
            await removeCategory(selectedCategory.id);
        }
        setDeleteOpen(false);
        setSelectedCategory(null);
    };

    // Cancel dialogs
    const handleEditCancel = () => {
        setEditOpen(false);
        setSelectedCategory(null);
    };
    const handleDeleteCancel = () => {
        setDeleteOpen(false);
        setSelectedCategory(null);
    };

    return (
        <>
            <TableContainer component={Paper} sx={{ marginTop: 2 }}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Category Name</TableCell>
                            <TableCell align="right">Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {categories.map((cat) => (
                            <TableRow key={cat.id}>
                                <TableCell>{cat.name}</TableCell>
                                <TableCell align="right">
                                    <Button
                                        size="small"
                                        variant="outlined"
                                        sx={{ mr: 1 }}
                                        onClick={() => handleEdit(cat)}
                                    >
                                        Edit
                                    </Button>
                                    <Button
                                        size="small"
                                        variant="outlined"
                                        color="error"
                                        onClick={() => handleDelete(cat)}
                                    >
                                        Delete
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            {/* Edit Dialog */}
            <Dialog open={editOpen} onClose={handleEditCancel}>
                <DialogTitle>Edit Category</DialogTitle>
                <DialogContent>
                    <TextField
                        label="Category Name"
                        value={editName}
                        onChange={e => setEditName(e.target.value)}
                        fullWidth
                        autoFocus
                        sx={{ mt: 2 }}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleEditCancel} variant="outlined">
                        Cancel
                    </Button>
                    <Button onClick={handleEditSave} variant="contained" disabled={!editName.trim()}>
                        Save
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Delete Dialog */}
            <Dialog open={deleteOpen} onClose={handleDeleteCancel}>
                <DialogTitle>Delete Category</DialogTitle>
                <DialogContent>
                    <Typography>
                        Are you sure you want to delete{" "}
                        <b>{selectedCategory?.name}</b>?
                    </Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleDeleteCancel} variant="outlined">
                        Cancel
                    </Button>
                    <Button onClick={handleDeleteConfirm} color="error" variant="contained">
                        Delete
                    </Button>
                </DialogActions>
            </Dialog>
        </>
    );
};

export default CategoryTable;