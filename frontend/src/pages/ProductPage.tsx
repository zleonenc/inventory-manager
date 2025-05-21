import { useState } from "react";
import ProductFilter from "../components/Product/ProductFilter";
import ProductTable from "../components/Product/ProductTable";
import InventoryMetrics from "../components/InventoryMetrics";
import CreateProduct from "../components/Product/CreateProduct";
import Container from "@mui/material/Container";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import CategoryPage from "./CategoryPage";

const ProductPage = () => {
    const [open, setOpen] = useState(false);
    const [showCategories, setShowCategories] = useState(false);

    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);

    return showCategories ? (
        <CategoryPage />
    ) : (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            <Typography variant="h4" gutterBottom>
                Inventory Manager
            </Typography>
            <Box mb={2} display="flex" justifyContent="flex-end" gap={2}>
                <Button variant="outlined" onClick={() => setShowCategories(true)}>
                    Categories
                </Button>
            </Box>
            <Box mb={3}>
                <ProductFilter />
            </Box>
            <Box mb={2} display="flex" justifyContent="flex-start" gap={2}>
                <Button variant="contained" onClick={handleOpen}>
                    Create Product
                </Button>
            </Box>
            <Box mb={3}>
                <ProductTable />
            </Box>
            <InventoryMetrics />
            <CreateProduct open={open} onClose={handleClose} />
        </Container>
    );
};

export default ProductPage;