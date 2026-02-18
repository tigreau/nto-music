import { expect, test, type Page } from '@playwright/test';

type Role = 'USER' | 'ADMIN';

interface MockUser {
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
}

interface MockApiOptions {
  authUser: MockUser | null;
}

interface MockApiState {
  addToCartCalls: number;
  checkoutCalls: number;
  updateProfileCalls: number;
}

function createDefaultUser(role: Role = 'USER'): MockUser {
  return {
    userId: role === 'ADMIN' ? 99 : 7,
    email: role === 'ADMIN' ? 'admin@nto.test' : 'user@nto.test',
    firstName: role === 'ADMIN' ? 'Admin' : 'User',
    lastName: 'Tester',
    role,
  };
}

async function mockApi(page: Page, options: MockApiOptions): Promise<MockApiState> {
  const state: MockApiState = {
    addToCartCalls: 0,
    checkoutCalls: 0,
    updateProfileCalls: 0,
  };

  let authUser = options.authUser;

  await page.route('**/*', async (route) => {
    const request = route.request();
    const method = request.method();
    const url = new URL(request.url());
    const path = url.pathname;

    if (!path.startsWith('/api/')) {
      return route.continue();
    }

    const json = (status: number, body: unknown) =>
      route.fulfill({ status, contentType: 'application/json', body: JSON.stringify(body) });

    if (path === '/api/auth/me' && method === 'GET') {
      if (!authUser) return json(401, { code: 'UNAUTHORIZED', message: 'Authentication required' });
      return json(200, { token: null, ...authUser });
    }

    if (path === '/api/auth/login' && method === 'POST') {
      const payload = (await request.postDataJSON()) as { email?: string };
      const role: Role = payload.email?.includes('admin') ? 'ADMIN' : 'USER';
      authUser = createDefaultUser(role);
      return json(200, { token: 'token', ...authUser });
    }

    if (path === '/api/auth/logout' && method === 'POST') {
      authUser = null;
      return route.fulfill({ status: 204 });
    }

    if (path === '/api/notifications' && method === 'GET') {
      return json(200, []);
    }

    if (path === '/api/notifications/stream' && method === 'GET') {
      return route.fulfill({
        status: 200,
        headers: {
          'content-type': 'text/event-stream',
          'cache-control': 'no-cache',
          connection: 'keep-alive',
        },
        body: 'event: connected\\ndata: connected\\n\\n',
      });
    }

    if (path === '/api/brands' && method === 'GET') {
      return json(200, [{ id: 1, name: 'Yamaha', slug: 'yamaha', logoUrl: null }]);
    }

    if (path === '/api/categories' && method === 'GET') {
      return json(200, [{ id: 1, name: 'Guitars', slug: 'guitars', productCount: 1, subCategories: [] }]);
    }

    if (path === '/api/products' && method === 'GET') {
      return json(200, {
        content: [
          {
            id: 10,
            name: 'Acoustic Guitar',
            slug: 'acoustic-guitar',
            price: 199,
            categoryName: 'Guitars',
            brandName: 'Yamaha',
            condition: 'NEW',
            thumbnailUrl: null,
            isPromoted: false,
          },
        ],
        totalElements: 1,
        totalPages: 1,
        number: 0,
        size: 20,
      });
    }

    if (path === '/api/carts/my/details' && method === 'GET') {
      return json(200, [
        {
          id: 1,
          quantity: 1,
          subTotal: 199,
          product: {
            id: 10,
            name: 'Acoustic Guitar',
            slug: 'acoustic-guitar',
            price: 199,
            thumbnailUrl: null,
            category: { id: 1, name: 'Guitars', slug: 'guitars' },
            brand: { id: 1, name: 'Yamaha', slug: 'yamaha' },
          },
        },
      ]);
    }

    if (path.startsWith('/api/carts/my/products/') && method === 'POST') {
      state.addToCartCalls += 1;
      return route.fulfill({ status: 204 });
    }

    if (path === '/api/orders/checkout' && method === 'POST') {
      state.checkoutCalls += 1;
      return json(200, { orderId: 501, totalAmount: 245.78, paymentStatus: 'PAID', transactionId: 'txn-smoke' });
    }

    if (path.startsWith('/api/users/') && method === 'GET') {
      return json(200, {
        id: authUser?.userId ?? 7,
        firstName: authUser?.firstName ?? 'User',
        lastName: authUser?.lastName ?? 'Tester',
        email: authUser?.email ?? 'user@nto.test',
        phoneNumber: '12345',
      });
    }

    if (path.startsWith('/api/users/') && method === 'PUT') {
      state.updateProfileCalls += 1;
      const payload = await request.postDataJSON();
      return json(200, payload);
    }

    return json(404, { code: 'RESOURCE_NOT_FOUND', message: `Unhandled mock route: ${method} ${path}` });
  });

  return state;
}

test('login flow works headlessly', async ({ page }) => {
  await mockApi(page, { authUser: null });

  await page.goto('/login');
  await expect(page.getByText('Welcome Back')).toBeVisible();
  await page.getByPlaceholder('Email address').fill('user@nto.test');
  await page.getByPlaceholder('Password').fill('secret123');
  await page.getByRole('button', { name: 'Sign In' }).click();

  await expect(page).toHaveURL('http://127.0.0.1:4173/');
  await expect(page.getByText('NTO MUSIC').first()).toBeVisible();
});

test('admin route blocks non-admin user', async ({ page }) => {
  await mockApi(page, { authUser: createDefaultUser('USER') });

  await page.goto('/admin');
  await expect(page.getByText('Restoring session...')).not.toBeVisible();
  await expect(page).toHaveURL('http://127.0.0.1:4173/');
});

test('add-to-cart flow calls API', async ({ page }) => {
  const state = await mockApi(page, { authUser: createDefaultUser('USER') });

  await page.goto('/');
  await expect(page.getByText('NTO MUSIC').first()).toBeVisible();
  await page.getByRole('button', { name: '+ Cart' }).first().click();

  await expect(page.getByText('Added to cart')).toBeVisible();
  expect(state.addToCartCalls).toBe(1);
});

test('checkout flow submits and shows confirmation', async ({ page }) => {
  const state = await mockApi(page, { authUser: createDefaultUser('USER') });

  await page.goto('/checkout');
  await expect(page.getByText('CHECKOUT')).toBeVisible();

  await page.getByPlaceholder('Main Street').fill('Main Street');
  await page.getByPlaceholder('42A').fill('42');
  await page.getByPlaceholder('10001').fill('12345');
  await page.getByPlaceholder('Amsterdam').fill('Amsterdam');
  await page.getByPlaceholder('Netherlands').fill('Netherlands');

  await page.getByRole('button', { name: /Place Order/ }).click();

  await expect(page.getByText('ORDER CONFIRMED')).toBeVisible();
  await expect(page.getByText('Thank you for your order!')).toBeVisible();
  expect(state.checkoutCalls).toBe(1);
});

test('profile update flow submits changes', async ({ page }) => {
  const state = await mockApi(page, { authUser: createDefaultUser('USER') });

  await page.goto('/user-profile');
  await expect(page.getByText('EDIT PROFILE')).toBeVisible();

  const firstName = page.getByPlaceholder('First Name');
  await firstName.fill('Updated');
  await page.getByRole('button', { name: 'Save Changes' }).click();

  await expect(page.getByText('Profile updated successfully')).toBeVisible();
  expect(state.updateProfileCalls).toBe(1);
});
