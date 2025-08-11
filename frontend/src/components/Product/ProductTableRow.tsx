import { memo } from "react";
import dayjs from "dayjs";

import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import Tooltip from "@mui/material/Tooltip";
import IconButton from "@mui/material/IconButton";
import Button from "@mui/material/Button";

import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import RemoveCircleIcon from "@mui/icons-material/RemoveCircle";

import {
    formatCurrency
} from "../../utils/format";
import {
    EXPIRY_THRESHOLDS,
    ROW_COLORS,
    STOCK_THRESHOLDS
} from "./rowConstants";
import {
    Product
} from "../../types/Product";


const getRowBgColor = (expirationDate: string | null | undefined) => {
    if (!expirationDate) return "inherit";

    const today = dayjs();
    const expiration = dayjs(expirationDate);
    const diff = expiration.diff(today, "day");

    if (diff < EXPIRY_THRESHOLDS.ALERT_DAYS) {
        return ROW_COLORS.EXPIRY_ALERT;
    }
    if (diff < EXPIRY_THRESHOLDS.WARN_DAYS) {
        return ROW_COLORS.EXPIRY_WARN;
    }
    if (diff >= EXPIRY_THRESHOLDS.WARN_DAYS) {
        return ROW_COLORS.EXPIRY_OK;
    }
    return "inherit";
};

const getStockCellColor = (stock: number) => {
    if (stock >= STOCK_THRESHOLDS.SAFE_MIN) {
        return ROW_COLORS.STOCK_SAFE;
    }
    if (stock >= STOCK_THRESHOLDS.WARN_MIN) {
        return ROW_COLORS.STOCK_WARN;
    }
    return ROW_COLORS.STOCK_LOW;
};

type Props = {
    product: Product;
    onEdit: (product: Product) => void;
    onDelete: (product: Product) => void;
    onToggleStock: (product: Product) => void;
};

const ProductTableRow = ({ product, onEdit, onDelete, onToggleStock }: Props) => {
    const isOutOfStock = product.stock === 0;
    const rowBgColor = getRowBgColor(product.expirationDate ?? null);
    const stockCellColor = getStockCellColor(product.stock);

    return (
        <TableRow
            key={product.id}
            sx={{ backgroundColor: rowBgColor }}
            role="row"
            aria-label={`Product ${product.name}`}
        >
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
                    {formatCurrency(product.price)}
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
                        ...(isOutOfStock ? { textDecoration: "line-through" } : {}),
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
                        aria-label={product.stock === 0 ? "Restore default stock" : "Set out of stock"}
                        onClick={() => onToggleStock(product)}
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
                    onClick={() => onEdit(product)}
                >
                    Edit
                </Button>
                <Button
                    size="small"
                    variant="contained"
                    color="error"
                    startIcon={<DeleteIcon />}
                    onClick={() => onDelete(product)}
                >
                    Delete
                </Button>
            </TableCell>
        </TableRow>
    );
};

export default memo(ProductTableRow);
