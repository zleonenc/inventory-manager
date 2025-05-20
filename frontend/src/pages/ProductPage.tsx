import ProductFilter from "../components/ProductFilter";
import ProductTable from "../components/ProductTable";
import InventoryMetrics from "../components/InventoryMetrics";
import Container from "@mui/material/Container";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";

const ProductPage = () => {
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        Inventory Manager
      </Typography>
      <Box mb={3}>
        <ProductFilter />
      </Box>
      <Box mb={3}>
        <ProductTable />
      </Box>
      <InventoryMetrics />
    </Container>
  );
};

export default ProductPage;