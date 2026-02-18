import { describe, expect, it, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { CartProvider, useCart } from '@/context/CartContext';

const { fetchCartItemsMock, useAuthMock } = vi.hoisted(() => ({
  fetchCartItemsMock: vi.fn(),
  useAuthMock: vi.fn(),
}));

vi.mock('@/api/client', () => ({
  fetchCartItems: fetchCartItemsMock,
}));

vi.mock('@/context/AuthContext', () => ({
  useAuth: useAuthMock,
}));

function CartProbe() {
  const { cartItems, cartTotalItems } = useCart();
  return (
    <div>
      <span data-testid="items">{cartItems.length}</span>
      <span data-testid="total">{cartTotalItems}</span>
    </div>
  );
}

describe('CartContext', () => {
  it('loads cart for authenticated users', async () => {
    useAuthMock.mockReturnValue({ isAuthenticated: true });
    fetchCartItemsMock.mockResolvedValue([
      {
        id: 1,
        quantity: 2,
        subTotal: 20,
        product: {
          id: 99,
          name: 'Pedal',
          slug: 'pedal',
          price: 10,
          thumbnailUrl: null,
          category: null,
          brand: null,
        },
      },
    ]);

    render(
      <CartProvider>
        <CartProbe />
      </CartProvider>,
    );

    await waitFor(() => expect(screen.getByTestId('items')).toHaveTextContent('1'));
    expect(screen.getByTestId('total')).toHaveTextContent('2');
  });

  it('keeps cart empty when not authenticated', async () => {
    useAuthMock.mockReturnValue({ isAuthenticated: false });

    render(
      <CartProvider>
        <CartProbe />
      </CartProvider>,
    );

    await waitFor(() => expect(screen.getByTestId('items')).toHaveTextContent('0'));
    expect(fetchCartItemsMock).not.toHaveBeenCalled();
  });
});
