import {
    useCallback,
    useState
} from "react";

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
import Typography from "@mui/material/Typography";

import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import CancelIcon from "@mui/icons-material/Cancel";

import styles from "./CategoryTable.module.css";

import {
    useCategoryContext
} from "../../context/CategoryContext";

import EditCategory from "./EditCategory";

import {
    Category
} from "../../types/Category";

const CategoryTable = () => {
    const { categories, editCategory, removeCategory } = useCategoryContext();
    const [editOpen, setEditOpen] = useState(false);
    const [deleteOpen, setDeleteOpen] = useState(false);
    const [selectedCategory, setSelectedCategory] = useState<Category | null>(null);
    const [editName, setEditName] = useState("");

    const handleEdit = useCallback((category: Category) => {
        setSelectedCategory(category);
        setEditName(category.name);
        setEditOpen(true);
    }, []);

    const handleDeleteClick = useCallback((category: Category) => {
        setSelectedCategory(category);
        setDeleteOpen(true);
    }, []);

    const handleDeleteConfirm = useCallback(async () => {
        if (selectedCategory) {
            await removeCategory(selectedCategory.id);
        }
        setDeleteOpen(false);
        setSelectedCategory(null);
    }, [selectedCategory, removeCategory]);

    const handleDeleteCancel = useCallback(() => {
        setDeleteOpen(false);
        setSelectedCategory(null);
    }, []);

    return (
        <>
            <TableContainer component={Paper} sx={{ marginTop: 2 }}>
                <Table aria-label="Categories table">
                    <TableHead>
                        <TableRow>
                            <TableCell align="center" colSpan={2} sx={{ fontSize: "1.5rem", fontWeight: "bold" }}>
                                Categories
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell className={styles.headerCell} scope="col">Category Name</TableCell>
                            <TableCell className={styles.headerCell} align="right" scope="col">Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {categories.map((cat) => (
                            <TableRow key={cat.id} role="row" aria-label={`Category ${cat.name}`}>
                                <TableCell>{cat.name}</TableCell>
                                <TableCell align="right">
                                    <Button
                                        size="small"
                                        variant="contained"
                                        sx={{ mr: 1 }}
                                        startIcon={<EditIcon />}
                                        aria-label={`Edit category ${cat.name}`}
                                        onClick={() => handleEdit(cat)}
                                    >
                                        Edit
                                    </Button>
                                    <Button
                                        size="small"
                                        variant="contained"
                                        color="error"
                                        startIcon={<DeleteIcon />}
                                        aria-label={`Delete category ${cat.name}`}
                                        onClick={() => handleDeleteClick(cat)}
                                    >
                                        Delete
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <EditCategory
                open={editOpen}
                onClose={() => setEditOpen(false)}
                category={selectedCategory}
            />

            <Dialog open={deleteOpen} onClose={handleDeleteCancel}>
                <DialogTitle>Delete Category</DialogTitle>
                <DialogContent>
                    <Typography>
                        Are you sure you want to delete{" "}
                        <b>{selectedCategory?.name}</b>?
                    </Typography>
                </DialogContent>
                <DialogActions>
                    <Button
                        onClick={handleDeleteCancel}
                        variant="outlined"
                        startIcon={<CancelIcon />}
                    >
                        Cancel
                    </Button>
                    <Button
                        onClick={handleDeleteConfirm}
                        color="error"
                        variant="contained"
                        startIcon={<DeleteIcon />}
                    >
                        Delete
                    </Button>
                </DialogActions>
            </Dialog>
        </>
    );
};

export default CategoryTable;