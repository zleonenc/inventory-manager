import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";

import {
    useProductContext
} from "../context/ProductContext";

import {
    formatCurrency,
    formatStock
} from "../utils/format";

import {
    Metric
} from "../types/Metric";


import styles from "./Product/ProductTable.module.css";

interface HeaderCellConfig {
    id: keyof Metric | string;
    label: string;
}

const headerCells: HeaderCellConfig[] = [
    { id: "categoryName", label: "Category" },
    { id: "totalStock", label: "Total Products in Stock" },
    { id: "totalValue", label: "Total Value in Stock" },
    { id: "averagePrice", label: "Average Price in Stock" },
];

const InventoryMetrics = () => {
    const { metrics } = useProductContext();

    return (
        <>
            <TableContainer component={Paper} sx={{ marginTop: 2 }}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell align="center" colSpan={4} sx={{ fontSize: "1.5rem", fontWeight: "bold" }}>
                                Inventory Metrics
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            {headerCells.map((headerCell) => (
                                <TableCell key={headerCell.id.toString()} className={styles.headerCell}>
                                    {headerCell.label}
                                </TableCell>
                            ))}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {metrics.map((metric) => (
                            <TableRow key={metric.categoryId}>
                                <TableCell>{metric.categoryName}</TableCell>
                                <TableCell>{formatStock(metric.totalStock)}</TableCell>
                                <TableCell>{formatCurrency(metric.totalValue)}</TableCell>
                                <TableCell>{formatCurrency(metric.averagePrice)}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </>
    );
};

export default InventoryMetrics;