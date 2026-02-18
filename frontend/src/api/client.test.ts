import { describe, expect, it } from 'vitest';
import { HttpResponse, http } from 'msw';
import { server } from '@/test/msw/server';
import {
  addToCart,
  fetchProducts,
  login,
  submitCheckout,
  verifySession,
} from '@/api/client';

describe('api client', () => {
  it('persists logged in user after login', async () => {
    server.use(
      http.post('/api/auth/login', async () =>
        HttpResponse.json({
          token: 'token',
          userId: 42,
          email: 'user@example.com',
          firstName: 'Test',
          lastName: 'User',
          role: 'USER',
        }),
      ),
    );

    const result = await login('user@example.com', 'password');

    expect(result.userId).toBe(42);
    expect(localStorage.getItem('user')).toContain('user@example.com');
  });

  it('clears session when verifySession fails', async () => {
    localStorage.setItem('user', JSON.stringify({ userId: 1, email: 'stale@example.com', role: 'USER' }));
    server.use(http.get('/api/auth/me', () => HttpResponse.json({ code: 'UNAUTHORIZED' }, { status: 401 })));

    const result = await verifySession();

    expect(result).toBeNull();
    expect(localStorage.getItem('user')).toBeNull();
  });

  it('sends expected query parameters for product listing', async () => {
    server.use(
      http.get('/api/products', ({ request }) => {
        const url = new URL(request.url);
        expect(url.searchParams.get('category')).toBe('guitars');
        expect(url.searchParams.get('minPrice')).toBe('100');
        expect(url.searchParams.get('sort')).toBe('price_asc');
        expect(url.searchParams.get('page')).toBe('2');
        expect(url.searchParams.get('size')).toBe('10');

        return HttpResponse.json({
          content: [],
          totalElements: 0,
          totalPages: 0,
          number: 2,
          size: 10,
        });
      }),
    );

    const result = await fetchProducts({
      category: 'guitars',
      minPrice: 100,
      sort: 'price_asc',
      page: 2,
      size: 10,
    });

    expect(result.number).toBe(2);
  });

  it('posts checkout payload and returns typed response', async () => {
    server.use(
      http.post('/api/orders/checkout', async ({ request }) => {
        const body = await request.json();
        expect(body).toMatchObject({ paymentMethod: 'CARD', city: 'Boston' });
        return HttpResponse.json({
          orderId: 100,
          totalAmount: 125,
          paymentStatus: 'PAID',
          transactionId: 'txn-1',
        });
      }),
    );

    const result = await submitCheckout({
      paymentMethod: 'CARD',
      street: 'Main',
      number: '12',
      postalCode: '02110',
      city: 'Boston',
      country: 'US',
    });

    expect(result.paymentStatus).toBe('PAID');
  });

  it('calls add-to-cart endpoint with path and query values', async () => {
    server.use(
      http.post('/api/carts/my/products/:productId', ({ params, request }) => {
        const url = new URL(request.url);
        expect(params.productId).toBe('7');
        expect(url.searchParams.get('quantity')).toBe('3');
        return new HttpResponse(null, { status: 204 });
      }),
    );

    await expect(addToCart(7, 3)).resolves.toBeUndefined();
  });
});
