import { describe, expect, it, vi } from 'vitest';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { act, renderHook } from '@testing-library/react';
import { ReactNode } from 'react';
import { useAddToCart } from '@/hooks/useApi';

const { addToCartMock } = vi.hoisted(() => ({
  addToCartMock: vi.fn(),
}));

vi.mock('@/api/client', async () => {
  const actual = await vi.importActual<object>('@/api/client');
  return {
    ...actual,
    addToCart: addToCartMock,
  };
});

describe('useApi mutations', () => {
  it('invalidates cart query after add-to-cart success', async () => {
    addToCartMock.mockResolvedValue(undefined);

    const queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false },
        mutations: { retry: false },
      },
    });
    const invalidateSpy = vi.spyOn(queryClient, 'invalidateQueries');

    const wrapper = ({ children }: { children: ReactNode }) => (
      <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    );

    const { result } = renderHook(() => useAddToCart(), { wrapper });

    await act(async () => {
      await result.current.mutateAsync({ productId: 10, quantity: 2 });
    });

    expect(addToCartMock).toHaveBeenCalledWith(10, 2);
    expect(invalidateSpy).toHaveBeenCalledWith({ queryKey: ['cartItems'] });
  });
});
