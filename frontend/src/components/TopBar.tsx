import React from "react";

import { Box, Typography } from "@mui/material";

const TopBar: React.FC = () => {
    return (
        <Box display="flex" alignItems="center" gap={2} mb={2}>
            <img
                src={require("../assets/encora-logo.webp")}
                alt="Logo"
                style={{ height: 100 }}
            />
            <Typography variant="h3" gutterBottom>
                Inventory Manager
            </Typography>
        </Box>
    );
};

export default TopBar;