import React from 'react';
import { render, screen } from '@testing-library/react';
import App from './App';

test('renders Inventory Manager title', () => {
    render(<App />);
    expect(screen.getByText(/Inventory Manager/i)).toBeInTheDocument();
});