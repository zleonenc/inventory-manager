import { useState, useEffect } from "react";
import { useProductContext } from "../../context/ProductContext";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import TablePagination from "@mui/material/TablePagination";
import EditProduct from "./EditProduct";
import { Product } from "../../types/Product";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import Typography from "@mui/material/Typography";
import TableSortLabel from "@mui/material/TableSortLabel";
import Alert from "@mui/material/Alert";
import Snackbar from "@mui/material/Snackbar";
import styles from "./ProductTable.module.css";
import dayjs from "dayjs";

const DEFAULT_ROWS_PER_PAGE = 10;

const getRowBgColor = (expirationDate: string | null | undefined) => {
    if (!expirationDate) return "inherit";

    const today = dayjs();
    const expiration = dayjs(expirationDate);
    const diff = expiration.diff(today, "day");

    if (diff < 7) {
        return "#ffcccb";
    }
    if (diff < 15) {
        return "#fff3cd";
    }
    if (diff >= 15) {
        return "#d1ecf1";
    }
}

const getStockCellColor = (stock: number) => {
    if (stock > 10) {
        return "white";
    }
    if (stock >= 5) {
        return "#FFD580";
    }
    if (stock < 5) {
        return "#ffcccb";
    }
};

const ProductTable = () => {
    const { products, total, fetchProducts, deleteProduct } = useProductContext();
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(DEFAULT_ROWS_PER_PAGE);
    const [editOpen, setEditOpen] = useState(false);
    const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);

    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [productToDelete, setProductToDelete] = useState<Product | null>(null);

    const [sortBy, setSortBy] = useState("name");
    const [sortDirection, setSortDirection] = useState<"asc" | "desc">("asc");

    const [sucessAlert, setSuccessAlert] = useState(false);

    useEffect(() => {
        fetchProducts({ page, size: rowsPerPage, sortBy, sortDirection });
    }, [page, rowsPerPage, sortBy, sortDirection]);

    const handleChangePage = (_event: unknown, newPage: number) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const handleEdit = (product: Product) => {
        setSelectedProduct(product);
        setEditOpen(true);
    };

    // Open the dialog and set the product to delete
    const handleDeleteClick = (product: Product) => {
        setProductToDelete(product);
        setDeleteDialogOpen(true);
    };

    // Confirm delete
    const handleConfirmDelete = async () => {
        if (productToDelete) {
            await deleteProduct(productToDelete.id);
            fetchProducts({ page, size: rowsPerPage });
            setSuccessAlert(true);
        }
        setDeleteDialogOpen(false);
        setProductToDelete(null);
    };

    // Cancel delete
    const handleCancelDelete = () => {
        setDeleteDialogOpen(false);
        setProductToDelete(null);
    };

    const handleSort = (column: string) => {
        if (sortBy === column) {
            setSortDirection(sortDirection === "asc" ? "desc" : "asc");
        } else {
            setSortBy(column);
            setSortDirection("asc");
        }
    };

    return (
        <>
            <TableContainer component={Paper} sx={{ marginBottom: 2 }}>
                <Table stickyHeader className={styles.productTable}>
                    <TableHead>
                        <TableRow sx={{ backgroundColor: "gray" }}>
                        <TableCell className={styles.headerCell}>
                                <TableSortLabel
                                    active={sortBy === "category"}
                                    direction={sortBy === "category" ? sortDirection : "asc"}
                                    IconComponent={() => (
                                        <span>
                                            {sortBy === "category" && sortDirection === "asc" && "▲"}
                                            {sortBy === "category" && sortDirection === "desc" && "▼"}
                                            {sortBy !== "category" && "▲▼"} 
                                        </span>
                                    )
                                    }
                                    onClick={() => handleSort("category")}
                                >
                                    Category
                                </TableSortLabel>
                            </TableCell>
                            <TableCell className={styles.headerCell}>
                                <TableSortLabel
                                    active={sortBy === "name"}
                                    direction={sortBy === "name" ? sortDirection : "asc"}
                                    IconComponent={() => (
                                        <span>
                                            {sortBy === "name" && sortDirection === "asc" && "▲"}
                                            {sortBy === "name" && sortDirection === "desc" && "▼"}
                                            {sortBy !== "name" && "▲▼"}
                                        </span>
                                    )}
                                    onClick={() => handleSort("name")}
                                >
                                    Name
                                </TableSortLabel>
                            </TableCell>
                            <TableCell className={styles.headerCell}>
                                <TableSortLabel
                                    active={sortBy === "price"}
                                    direction={sortBy === "price" ? sortDirection : "asc"}
                                    IconComponent={() => (
                                        <span>
                                            {sortBy === "price" && sortDirection === "asc" && "▲"}
                                            {sortBy === "price" && sortDirection === "desc" && "▼"}
                                            {sortBy !== "price" && "▲▼"}
                                        </span>
                                    )}
                                    onClick={() => handleSort("price")}
                                >
                                    Price
                                </TableSortLabel>
                            </TableCell>
                            <TableCell className={styles.headerCell}>
                                <TableSortLabel
                                    active={sortBy === "expirationdate"}
                                    direction={sortBy === "expirationdate" ? sortDirection : "asc"}
                                    IconComponent={() => (
                                        <span>
                                            {sortBy === "expirationdate" && sortDirection === "asc" && "▲"}
                                            {sortBy === "expirationdate" && sortDirection === "desc" && "▼"}
                                            {sortBy !== "expirationdate" && "▲▼"}
                                        </span>
                                    )}
                                    onClick={() => handleSort("expirationdate")}
                                >
                                    Expiration Date
                                </TableSortLabel>
                            </TableCell>
                            <TableCell className={styles.headerCell}>
                                <TableSortLabel
                                    active={sortBy === "stock"}
                                    direction={sortBy === "stock" ? sortDirection : "asc"}
                                    IconComponent={() => (
                                        <span>
                                            {sortBy === "stock" && sortDirection === "asc" && "▲"}
                                            {sortBy === "stock" && sortDirection === "desc" && "▼"}
                                            {sortBy !== "stock" && "▲▼"}
                                        </span>
                                    )}
                                    onClick={() => handleSort("stock")}
                                >
                                    Stock
                                </TableSortLabel>
                            </TableCell>
                            <TableCell className={styles.headerCell}>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {products.map((product) => {
                            const isOutOfStock = product.stock === 0;
                            const rowBgColor = getRowBgColor(product.expirationDate ?? null);
                            const stockCellColor = getStockCellColor(product.stock);

                            return (
                                <TableRow
                                    key={product.id}
                                    sx={{
                                        backgroundColor: rowBgColor,
                                    }}>
                                    <TableCell>
                                        <span style={isOutOfStock ? { textDecoration: "line-through" } : {}}>
                                            {product.category.name}
                                        </span>
                                    </TableCell>
                                    <TableCell>
                                        <span style={isOutOfStock ? { textDecoration: "line-through" } : {}}>
                                            {product.name}
                                        </span>
                                    </TableCell>
                                    <TableCell>
                                        <span style={isOutOfStock ? { textDecoration: "line-through" } : {}}>
                                            ${product.price.toFixed(2)}
                                        </span>
                                    </TableCell>
                                    <TableCell>
                                        <span style={isOutOfStock ? { textDecoration: "line-through" } : {}}>
                                            {product.expirationDate || "N/A"}
                                        </span>
                                    </TableCell>
                                    <TableCell sx={{ backgroundColor: stockCellColor }}>
                                        <span style={isOutOfStock ? { textDecoration: "line-through" } : {}}>
                                            {product.stock}
                                        </span>
                                    </TableCell>
                                    <TableCell>
                                        <Button
                                            size="small"
                                            variant="outlined"
                                            sx={{ mr: 1 }}
                                            onClick={() => handleEdit(product)}
                                        >
                                            Edit
                                        </Button>
                                        <Button
                                            size="small"
                                            variant="outlined"
                                            color="error"
                                            onClick={() => handleDeleteClick(product)}
                                        >
                                            Delete
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            );
                        })}
                    </TableBody>
                </Table>
                <TablePagination
                    component="div"
                    count={total}
                    page={page}
                    onPageChange={handleChangePage}
                    rowsPerPage={rowsPerPage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                    rowsPerPageOptions={[5, 10, 25, 50]}
                    sx={{
                        display: "flex",
                        justifyContent: "center",
                    }}
                />
            </TableContainer>
            <EditProduct
                open={editOpen}
                onClose={() => setEditOpen(false)}
                product={selectedProduct}
            />

            {/* Delete Confirmation Dialog */}
            <Dialog open={deleteDialogOpen} onClose={handleCancelDelete}>
                <DialogTitle>Delete Product</DialogTitle>
                <DialogContent>
                    <Typography>
                        Are you sure you want to delete{" "}
                        <b>{productToDelete?.name}</b>?
                    </Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCancelDelete} variant="outlined">
                        Cancel
                    </Button>
                    <Button onClick={handleConfirmDelete} color="error" variant="contained">
                        Delete
                    </Button>
                </DialogActions>
            </Dialog>

            <Snackbar
                open={sucessAlert}
                autoHideDuration={2500}
                onClose={() => setSuccessAlert(false)}
                anchorOrigin={{ vertical: "top", horizontal: "center" }}
            >
                <Alert onClose={() => setSuccessAlert(false)} severity="success" sx={{ width: '100%' }}>
                    Product deleted successfully!
                </Alert>
            </Snackbar>
        </>
    );
};

export default ProductTable;