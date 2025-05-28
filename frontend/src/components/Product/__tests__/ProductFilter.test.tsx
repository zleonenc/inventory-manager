import React from 'react';
import { render, screen } from '@testing-library/react';
import ProductFilter from '../ProductFilter';
import { ProductProvider } from '../../../context/ProductContext';
import { CategoryProvider } from '../../../context/CategoryContext';

test('renders ProductFilter fields', () => {
    render(
        <CategoryProvider>
            <ProductProvider>
                <ProductFilter />
            </ProductProvider>
        </CategoryProvider>
    );
    expect(screen.getByLabelText(/Name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Category/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Availability/i)).toBeInTheDocument();
});