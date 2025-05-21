import { useState } from "react";
import Container from "@mui/material/Container";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import CategoryTable from "../components/Category/CategoryTable";
import CreateCategory from "../components/Category/CreateCategory";
import ProductPage from "./ProductPage";

const CategoryPage = () => {
    const [open, setOpen] = useState(false);
    const [showProducts, setShowProducts] = useState(false);

    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);

    return showProducts ? (
        <ProductPage />
    ) : (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            <Typography variant="h4" gutterBottom>
                Inventory Manager
            </Typography>
            <Box mb={2} display="flex" justifyContent="flex-end" gap={2}>
                <Button variant="outlined" onClick={() => setShowProducts(true)}>
                    Products
                </Button>
            </Box>
            <Box mb={2} display="flex" justifyContent="flex-start" gap={2}>
                <Button variant="contained" onClick={handleOpen}>
                    Create Category
                </Button>
            </Box>
            <CategoryTable />
            <CreateCategory open={open} onClose={handleClose} />
        </Container>
    );
};

export default CategoryPage;