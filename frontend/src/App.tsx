import {BrowserRouter as Router, Routes, Route} from "react-router-dom";
import { ProductProvider } from "./context/ProductContext";
import ProductList from "./pages/ProductList";

const App = () => {
  return (
    <ProductProvider>
      <Router>
        <Routes>
          <Route path="/products" element={<ProductList/>}/>
        </Routes>
      </Router>
    </ProductProvider>
  )
}

export default App;
