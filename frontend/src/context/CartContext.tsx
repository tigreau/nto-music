import { createContext, useContext, useState, useEffect, ReactNode, useCallback } from 'react';
import { fetchCartItems } from '@/api/client';
import { CartItem } from '@/types';
import { useAuth } from '@/context/AuthContext';
import { toUnknownApiError, type ApiError } from '@/lib/apiError';

interface CartContextType {
    cartItems: CartItem[];
    cartTotalItems: number;
    isLoading: boolean;
    cartError: ApiError | null;
    clearCartError: () => void;
    refreshCart: () => Promise<void>;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider = ({ children }: { children: ReactNode }) => {
    const [cartItems, setCartItems] = useState<CartItem[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [cartError, setCartError] = useState<ApiError | null>(null);
    const { isAuthenticated } = useAuth();

    const fetchCart = useCallback(async () => {
        setIsLoading(true);
        try {
            if (!isAuthenticated) {
                setCartItems([]);
                setCartError(null);
                return;
            }
            const data = await fetchCartItems();
            setCartItems(data);
            setCartError(null);
        } catch (error) {
            setCartError(toUnknownApiError(error));
        } finally {
            setIsLoading(false);
        }
    }, [isAuthenticated]);

    useEffect(() => {
        fetchCart();
    }, [fetchCart]);

    const cartTotalItems = cartItems.reduce((acc, item) => acc + item.quantity, 0);

    return (
        <CartContext.Provider
            value={{
                cartItems,
                cartTotalItems,
                isLoading,
                cartError,
                clearCartError: () => setCartError(null),
                refreshCart: fetchCart,
            }}
        >
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
