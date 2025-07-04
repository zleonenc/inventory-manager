import { useState } from "react";

import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import InputLabel from "@mui/material/InputLabel";
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import Select from "@mui/material/Select";
import Button from "@mui/material/Button";
import OutlinedInput from "@mui/material/OutlinedInput";
import SearchIcon from "@mui/icons-material/Search";

import { useProductContext } from "../../context/ProductContext";
import { useCategoryContext } from "../../context/CategoryContext";

const ProductFilter = () => {
    const { fetchProducts } = useProductContext();
    const { categories } = useCategoryContext();
    const [searchTerm, setSearchTerm] = useState("");
    const [categoryIds, setCategoryIds] = useState<number[]>([]);
    const [availability, setAvailability] = useState("");

    const handleFilter = () => {
        fetchProducts({
            name: searchTerm,
            categories: categoryIds,
            available: availability,
        });
    };

    const handleCategoryChange = (event: any) => {
        const {
            target: { value },
        } = event;
        if (value.includes("")) {
            setCategoryIds([]);
        } else {
            setCategoryIds(typeof value === "string" ? value.split(",").map(Number) : value);
        }
    };

    return (
        <Box
            display="flex"
            alignItems="center"
            gap={2}
            mb={2}
            sx={{
                border: "1px solid #ccc",
                borderRadius: 2,
                padding: 2,
                backgroundColor: "#fafafa",
            }}
        >
            <TextField
                label="Name"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                size="small"
            />
            <FormControl size="small" sx={{ minWidth: 180, maxWidth: 180 }}>
                <InputLabel id="category-label" shrink={true}>
                    Category
                </InputLabel>
                <Select
                    labelId="category-label"
                    multiple
                    value={categoryIds}
                    onChange={handleCategoryChange}
                    input={<OutlinedInput label="Category" />}
                    displayEmpty
                    renderValue={(selected) => (
                        <span
                            style={{
                                display: "block",
                                whiteSpace: "nowrap",
                                overflow: "hidden",
                                textOverflow: "ellipsis",
                                maxWidth: 160,
                            }}
                        >
                            {categoryIds.length === 0
                                ? <em>All</em>
                                : categories
                                    .filter((cat) => selected.includes(cat.id))
                                    .map((cat) => cat.name)
                                    .join(", ")
                            }
                        </span>
                    )}
                >
                    <MenuItem value="">
                        <em>All</em>
                    </MenuItem>
                    {categories.map((category) => (
                        <MenuItem key={category.id} value={category.id}>
                            {category.name}
                        </MenuItem>
                    ))}
                </Select>
            </FormControl>
            <FormControl size="small" sx={{ minWidth: 150 }}>
                <InputLabel id="availability-label" shrink={true}>
                    Availability
                </InputLabel>
                <Select
                    labelId="availability-label"
                    value={availability}
                    label="Availability"
                    onChange={(e) => setAvailability(e.target.value)}
                    displayEmpty
                    renderValue={(selected) =>
                        !selected ? <em>All</em> : selected === "instock" ? "In Stock" : "Out of Stock"
                    }
                >
                    <MenuItem value="">
                        <em>All</em>
                    </MenuItem>
                    <MenuItem value="instock">In Stock</MenuItem>
                    <MenuItem value="outofstock">Out of Stock</MenuItem>
                </Select>
            </FormControl>
            <Button
                variant="contained"
                onClick={handleFilter}
                startIcon={<SearchIcon />}
            >
                Search
            </Button>
        </Box>
    );
};

export default ProductFilter;