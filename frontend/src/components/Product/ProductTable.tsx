import {
    useState,
    useEffect,
    useMemo,
    useCallback,
    useRef
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
import TableSortLabel from "@mui/material/TableSortLabel";
import Alert from "@mui/material/Alert";
import Snackbar from "@mui/material/Snackbar";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import FormControl from "@mui/material/FormControl";
import InputLabel from "@mui/material/InputLabel";
import Box from "@mui/material/Box";

import DeleteIcon from "@mui/icons-material/Delete";
import Pagination from "@mui/material/Pagination";
import CancelIcon from "@mui/icons-material/Cancel";

import styles from "./ProductTable.module.css";

import {
    useProductContext
} from "../../context/ProductContext";

import {
    usePagination
} from "../../hooks/usePagination";

import ProductTableRow from "./ProductTableRow";
import EditProduct from "./EditProduct";

import {
    Product
} from "../../types/Product";

const DEFAULT_ROWS_PER_PAGE = 10;
const ROWS_PER_PAGE_OPTIONS = [5, 10, 25, 50];

const ProductTable = () => {
    const { products, total, fetchProducts, deleteProduct, setProductInStock, setProductOutOfStock, lastFilters } = useProductContext();
    const { page, setPage, rowsPerPage, onRowsPerPageChange, pageCount, rowsPerPageOptions } = usePagination({ total, initialRowsPerPage: DEFAULT_ROWS_PER_PAGE, rowsPerPageOptions: ROWS_PER_PAGE_OPTIONS });
    const [editOpen, setEditOpen] = useState(false);
    const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);

    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [productToDelete, setProductToDelete] = useState<Product | null>(null);

    const [primarySortBy, setPrimarySortBy] = useState<string | null>("name");
    const [primarySortDirection, setPrimaryDirection] = useState<"asc" | "desc">("asc");
    const [secondarySortBy, setSecondarySortBy] = useState<string | null>(null);
    const [secondarySortDirection, setSecondarySortDirection] = useState<"asc" | "desc">("asc");

    const [successAlert, setSuccessAlert] = useState(false);

    const params = useMemo(() => {
        const p: any = { ...lastFilters, page, size: rowsPerPage };
        if (primarySortBy) {
            p.primarySortBy = primarySortBy;
            p.primarySortDirection = primarySortDirection;
        }
        if (secondarySortBy && primarySortBy) {
            p.secondarySortBy = secondarySortBy;
            p.secondarySortDirection = secondarySortDirection;
        }
        return p;
    }, [lastFilters, page, rowsPerPage, primarySortBy, primarySortDirection, secondarySortBy, secondarySortDirection]);

    const lastParamsRef = useRef<string | null>(null);
    useEffect(() => {
        const signature = JSON.stringify(params);
        if (lastParamsRef.current === signature) {
            return;
        }
        lastParamsRef.current = signature;
        fetchProducts(params);
    }, [params, fetchProducts]);

    const handleEdit = useCallback((product: Product) => {
        setSelectedProduct(product);
        setEditOpen(true);
    }, []);

    const handleDeleteClick = useCallback((product: Product) => {
        setProductToDelete(product);
        setDeleteDialogOpen(true);
    }, []);

    const handleDeleteConfirm = useCallback(async () => {
        if (productToDelete) {
            await deleteProduct(productToDelete.id);
            fetchProducts(params);
            setSuccessAlert(true);
        }
        setDeleteDialogOpen(false);
        setProductToDelete(null);
    }, [productToDelete, deleteProduct, fetchProducts, params]);

    const handleDeleteCancel = useCallback(() => {
        setDeleteDialogOpen(false);
        setProductToDelete(null);
    }, []);

    const handleSort = useCallback((column: string) => {
        if (primarySortBy === column) {
            setPrimaryDirection(primarySortDirection === "asc" ? "desc" : "asc");
            if (primarySortDirection === "desc") {
                setPrimarySortBy(secondarySortBy);
                setPrimaryDirection(secondarySortBy ? secondarySortDirection : "asc");
                setSecondarySortBy(null);
                setSecondarySortDirection("asc");
            }
        } else if (secondarySortBy === column) {
            setSecondarySortDirection(secondarySortDirection === "asc" ? "desc" : "asc");
            if (secondarySortDirection === "desc") {
                setSecondarySortBy(null);
                setSecondarySortDirection("asc");
            }
        } else {
            if (primarySortBy === null) {
                setPrimarySortBy(column);
                setPrimaryDirection("asc");
                setSecondarySortBy(null);
            } else {
                setSecondarySortBy(column);
                setSecondarySortDirection("asc");
            }
        }
    }, [primarySortBy, primarySortDirection, secondarySortBy, secondarySortDirection]);

    const columns = [
        { id: "category", label: "Category" },
        { id: "name", label: "Name" },
        { id: "price", label: "Price" },
        { id: "expirationdate", label: "Expiration Date" },
        { id: "stock", label: "Stock" },
    ];

    return (
        <>
            <TableContainer component={Paper} sx={{ marginBottom: 2 }}>
                <Table stickyHeader className={styles.productTable}>
                    <TableHead aria-label="Products table header">
                        <TableRow>
                            <TableCell align="center" colSpan={7} sx={{ fontSize: "1.5rem", fontWeight: "bold" }}>
                                Products
                            </TableCell>
                        </TableRow>
                        <TableRow sx={{ backgroundColor: "gray" }}>
                            {columns.map((column) => {
                                const columnId = column.id;
                                const isPrimarySort = primarySortBy === columnId;
                                const isSecondarySort = secondarySortBy === columnId;
                                let iconSymbol = "↕"; // Default

                                if (isPrimarySort) {
                                    iconSymbol = primarySortDirection === "asc" ? "▲" : "▼";
                                } else if (isSecondarySort) {
                                    iconSymbol = secondarySortDirection === "asc" ? "▲" : "▼";
                                }
                                return (
                                    <TableCell
                                        key={columnId}
                                        className={styles.headerCell}
                                        sx={{
                                            ...(isPrimarySort && { borderTop: '3px solid #1976d2' }),
                                            ...(isSecondarySort && { borderTop: '4px double #4caf50' }),
                                        }}
                                    >
                                        <TableSortLabel
                                            active={isPrimarySort || isSecondarySort}
                                            direction={isPrimarySort ? primarySortDirection : (isSecondarySort ? secondarySortDirection : "asc")}
                                            IconComponent={() => <span>{iconSymbol}</span>}
                                            sx={
                                                !(isPrimarySort || isSecondarySort) ? {
                                                    '& .MuiTableSortLabel-icon': { opacity: 1 }
                                                } : {}
                                            }
                                            onClick={() => handleSort(columnId)}
                                            aria-label={`Sort by ${column.label}`}
                                        >
                                            {column.label}
                                        </TableSortLabel>
                                    </TableCell>
                                );
                            })}
                            <TableCell className={styles.headerCell}>Toggle Stock</TableCell>
                            <TableCell className={styles.headerCell}>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody role="rowgroup">
                        {products.map((product) => (
                            <ProductTableRow
                                key={product.id}
                                product={product}
                                onEdit={handleEdit}
                                onDelete={handleDeleteClick}
                                onToggleStock={(p) => p.stock === 0 ? setProductInStock(p.id) : setProductOutOfStock(p.id)}
                            />
                        ))}
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
                            aria-label="Products pagination"
                            count={pageCount}
                            page={page + 1}
                            onChange={(_e, value) => setPage(value - 1)}
                            color="primary"
                            showFirstButton
                            showLastButton
                            size="medium"
                            boundaryCount={1}
                            siblingCount={0}
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
                                onChange={onRowsPerPageChange}
                                aria-label="Rows per page"
                            >
                                {rowsPerPageOptions.map((size) => (
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
                open={successAlert}
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