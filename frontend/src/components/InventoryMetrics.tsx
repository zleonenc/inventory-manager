import { useProductContext } from "../context/ProductContext";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";

type Metric = {
  categoryName: string;
  totalStock: number;
  totalValue: number;
  averagePrice: number;
};

const InventoryMetrics = () => {
  const { metrics } = useProductContext();

  return (
    <TableContainer component={Paper} sx={{ marginTop: 2 }}>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Category</TableCell>
            <TableCell>Total Products in Stock</TableCell>
            <TableCell>Total Value in Stock</TableCell>
            <TableCell>Average Price in Stock</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {metrics.map((metric, index) => (
            <TableRow key={index}>
              <TableCell>{metric.categoryName}</TableCell>
              <TableCell>{metric.totalStock}</TableCell>
              <TableCell>${metric.totalValue.toFixed(2)}</TableCell>
              <TableCell>${metric.averagePrice.toFixed(2)}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default InventoryMetrics;