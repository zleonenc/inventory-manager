import { useState, useEffect } from "react";

import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import TablePagination from "@mui/material/TablePagination";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import Typography from "@mui/material/Typography";
import TableSortLabel from "@mui/material/TableSortLabel";
import Alert from "@mui/material/Alert";
import Snackbar from "@mui/material/Snackbar";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import Pagination from "@mui/material/Pagination";
import RemoveCircleIcon from "@mui/icons-material/RemoveCircle";
import Tooltip from "@mui/material/Tooltip";
import IconButton from "@mui/material/IconButton";
import CancelIcon from "@mui/icons-material/Cancel";
import dayjs from "dayjs";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import FormControl from "@mui/material/FormControl";
import InputLabel from "@mui/material/InputLabel";
import Box from "@mui/material/Box";

import { useProductContext } from "../../context/ProductContext";
import EditProduct from "./EditProduct";
import { Product } from "../../types/Product";
import styles from "./ProductTable.module.css";

const DEFAULT_ROWS_PER_PAGE = 10;

const getRowBgColor = (expirationDate: string | null | undefined) => {
    if (!expirationDate) return "inherit";

    const today = dayjs();
    const expiration = dayjs(expirationDate);
    const diff = expiration.diff(today, "day");

    if (diff < 7) {
        return "#F6D4D2";
    }
    if (diff < 15) {
        return "#FFFFBF";
    }
    if (diff >= 15) {
        return "#E4FAE4";
    }
}

const getStockCellColor = (stock: number) => {
    if (stock > 10) {
        return "white";
    }
    if (stock >= 5) {
        return "#FFB343";
    }
    if (stock < 5) {
        return "#ffb09c";
    }
};

const ProductTable = () => {
    const { products, total, fetchProducts, deleteProduct, setProductInStock, setProductOutOfStock } = useProductContext();
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

    const handleChangeRowsPerPage = (event: any) => {
        setRowsPerPage(Number(event.target.value));
        setPage(0);
    };

    const handleEdit = (product: Product) => {
        setSelectedProduct(product);
        setEditOpen(true);
    };

    const handleDeleteClick = (product: Product) => {
        setProductToDelete(product);
        setDeleteDialogOpen(true);
    };

    const handleDeleteConfirm = async () => {
        if (productToDelete) {
            await deleteProduct(productToDelete.id);
            fetchProducts({ page, size: rowsPerPage });
            setSuccessAlert(true);
        }
        setDeleteDialogOpen(false);
        setProductToDelete(null);
    };

    const handleDeleteCancel = () => {
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
                            <TableCell className={styles.headerCell}>Toggle Stock</TableCell>
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
                                    <TableCell>
                                        <span
                                            style={{
                                                display: "inline-block",
                                                padding: "5px 10px",
                                                borderRadius: "5px",
                                                ...isOutOfStock ? { textDecoration: "line-through" } : {},
                                                backgroundColor: stockCellColor,
                                                color: "black",
                                            }}
                                        >
                                            {product.stock}
                                        </span>
                                    </TableCell>
                                    <TableCell>
                                        <Tooltip title={product.stock === 0 ? "Restore Default Stock" : "Set Out of Stock"}>
                                            <IconButton
                                                size="small"
                                                color={product.stock === 0 ? "success" : "warning"}
                                                onClick={() =>
                                                    product.stock === 0
                                                        ? setProductInStock(product.id)
                                                        : setProductOutOfStock(product.id)
                                                }
                                            >
                                                {product.stock === 0 ? <CheckCircleIcon /> : <RemoveCircleIcon />}
                                            </IconButton>
                                        </Tooltip>
                                    </TableCell>
                                    <TableCell>
                                        <Button
                                            size="small"
                                            variant="contained"
                                            sx={{ mr: 1 }}
                                            startIcon={<EditIcon />}
                                            onClick={() => handleEdit(product)}
                                        >
                                            Edit
                                        </Button>
                                        <Button
                                            size="small"
                                            variant="contained"
                                            color="error"
                                            startIcon={<DeleteIcon />}
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
                <Box
                    sx={{
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "space-between",
                        gap: 2,
                        mb: 1,
                        mt: 1,
                    }}
                >
                    <Box sx={{ flex: 1 }} />

                    <Box sx={{ flex: 1, display: "flex", justifyContent: "center" }}>
                        <Pagination
                            count={Math.ceil(total / rowsPerPage)}
                            page={page + 1}
                            onChange={(_e, value) => setPage(value - 1)}
                            color="primary"
                            showFirstButton
                            showLastButton
                            size="medium"
                            shape="rounded"
                        />
                    </Box>

                    <Box sx={{ flex: 1, display: "flex", justifyContent: "flex-end", pr: 2 }}>
                        <FormControl size="small" sx={{ minWidth: 120 }}>
                            <InputLabel id="rows-per-page-label">Rows per page</InputLabel>
                            <Select
                                labelId="rows-per-page-label"
                                value={rowsPerPage}
                                label="Rows per page"
                                onChange={handleChangeRowsPerPage}
                            >
                                {[5, 10, 25, 50].map((size) => (
                                    <MenuItem key={size} value={size}>
                                        {size}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Box>
                </Box>
            </TableContainer>
            <EditProduct
                open={editOpen}
                onClose={() => setEditOpen(false)}
                product={selectedProduct}
            />

            {/* Delete Confirmation Dialog */}
            <Dialog open={deleteDialogOpen} onClose={handleDeleteCancel}>
                <DialogTitle>Delete Product</DialogTitle>
                <DialogContent>
                    <Typography>
                        Are you sure you want to delete{" "}
                        <b>{productToDelete?.name}</b>?
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