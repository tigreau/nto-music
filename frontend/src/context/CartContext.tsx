import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { getToken } from '@/api/client';

interface CartItem {
    id: number;
    quantity: number;
    products: any; // Using any for simplicity as we just need quantity for badge
}

interface CartContextType {
    cartItems: CartItem[];
    cartTotalItems: number;
    refreshCart: () => void;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider = ({ children }: { children: ReactNode }) => {
    const [cartItems, setCartItems] = useState<CartItem[]>([]);

    // Hardcoded cart ID 1 as per existing implementation
    const fetchCart = async () => {
        try {
            const token = getToken();
            if (!token) return;
            const response = await fetch('/api/carts/1/details', {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const data = await response.json();
                setCartItems(data);
            }
        } catch (error) {
            console.error("Failed to fetch cart", error);
        }
    };

    useEffect(() => {
        fetchCart();
    }, []);

    const cartTotalItems = cartItems.reduce((acc, item) => acc + item.quantity, 0);

    return (
        <CartContext.Provider value={{ cartItems, cartTotalItems, refreshCart: fetchCart }}>
            {children}
        </CartContext.Provider>
    );
};

export const useCart = () => {
    const context = useContext(CartContext);
    if (!context) {
        throw new Error('useCart must be used within a CartProvider');
    }
    return context;
};
