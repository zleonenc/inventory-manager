import React from 'react';
import { render, screen } from '@testing-library/react';
import ProductTable from '../ProductTable';
import { ProductProvider } from '../../../context/ProductContext';
import { CategoryProvider } from '../../../context/CategoryContext';

test('renders ProductTable headers', () => {
    render(
        <ProductProvider>
            <CategoryProvider>
            <ProductTable />
            </CategoryProvider>
        </ProductProvider>
    );
    expect(screen.getByText(/Category/i)).toBeInTheDocument();
    expect(screen.getByText(/Name/i)).toBeInTheDocument();
    expect(screen.getByText(/Price/i)).toBeInTheDocument();
    expect(screen.getByText(/Expiration Date/i)).toBeInTheDocument();
    expect(screen.getAllByText(/Stock/i).length).toBeGreaterThan(1);
    expect(screen.getByText(/Toggle Stock/i)).toBeInTheDocument();
    expect(screen.getByText(/Actions/i)).toBeInTheDocument();
});