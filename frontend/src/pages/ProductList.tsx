import { useProductContext } from "../context/ProductContext";
import Typography from "@mui/material/Typography";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import Paper from "@mui/material/Paper";

const ProductList = () => {
  const { products } = useProductContext();

  return (
    <Paper sx={{ p: 2 }}>
      <Typography variant="h5" gutterBottom>
        Product List
      </Typography>
      <List>
        {products.map((p) => (
          <ListItem key={p.id} divider>
            <ListItemText
              primary={p.name}
              secondary={`Stock: ${p.stock} | Active: ${p.active ? "Yes" : "No"} | Category: ${p.category.name}`}
            />
          </ListItem>
        ))}
      </List>
    </Paper>
  );
};

export default ProductList;