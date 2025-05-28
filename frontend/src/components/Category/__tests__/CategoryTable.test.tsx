import React from 'react';
import { render, screen } from '@testing-library/react';
import CategoryTable from '../CategoryTable';
import { CategoryProvider } from '../../../context/CategoryContext';

test('renders CategoryTable headers', () => {
    render(
        <CategoryProvider>
            <CategoryTable />
        </CategoryProvider>
    );
    expect(screen.getByText(/Category Name/i)).toBeInTheDocument();
    expect(screen.getByText(/Actions/i)).toBeInTheDocument();
});