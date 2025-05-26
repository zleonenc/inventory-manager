import { BrowserRouter as Router, Routes, Route } from "react-router";
import { ProductProvider } from "./context/ProductContext";
import { CategoryProvider } from "./context/CategoryContext";
import ProductPage from "./pages/ProductPage";
import { ThemeProvider, CssBaseline, createTheme } from "@mui/material";

const theme = createTheme();

const App = () => {
    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />
            <CategoryProvider>
                <ProductProvider>
                    <Router>
                        <Routes>
                            <Route path="/" element={<ProductPage />} />
                        </Routes>
                    </Router>
                </ProductProvider>
            </CategoryProvider>
        </ThemeProvider>
    );
};

export default App;
