import { describe, expect, it, vi } from 'vitest';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { render, screen } from '@testing-library/react';
import ProtectedRoute from '@/components/ProtectedRoute';
import { useAuth } from '@/context/AuthContext';

vi.mock('@/context/AuthContext', () => ({
  useAuth: vi.fn(),
}));

const mockedUseAuth = vi.mocked(useAuth);

function renderRoute(adminOnly = false) {
  return render(
    <MemoryRouter initialEntries={['/admin']}>
      <Routes>
        <Route
          path="/admin"
          element={(
            <ProtectedRoute adminOnly={adminOnly}>
              <div>Protected Content</div>
            </ProtectedRoute>
          )}
        />
        <Route path="/" element={<div>Home Page</div>} />
        <Route path="/login" element={<div>Login Page</div>} />
      </Routes>
    </MemoryRouter>,
  );
}

describe('ProtectedRoute', () => {
  it('shows loading state while auth is initializing', () => {
    mockedUseAuth.mockReturnValue({
      user: null,
      isInitializing: true,
      isAuthenticated: false,
      isAdmin: false,
      login: vi.fn(),
      register: vi.fn(),
      logout: vi.fn(),
      refreshSession: vi.fn(),
    });

    renderRoute();
    expect(screen.getByText('Checking session...')).toBeInTheDocument();
  });

  it('redirects unauthenticated users to login', () => {
    mockedUseAuth.mockReturnValue({
      user: null,
      isInitializing: false,
      isAuthenticated: false,
      isAdmin: false,
      login: vi.fn(),
      register: vi.fn(),
      logout: vi.fn(),
      refreshSession: vi.fn(),
    });

    renderRoute();
    expect(screen.getByText('Login Page')).toBeInTheDocument();
  });

  it('redirects non-admin users away from admin-only routes', () => {
    mockedUseAuth.mockReturnValue({
      user: { userId: 1, email: 'user@example.com', firstName: 'User', role: 'USER' },
      isInitializing: false,
      isAuthenticated: true,
      isAdmin: false,
      login: vi.fn(),
      register: vi.fn(),
      logout: vi.fn(),
      refreshSession: vi.fn(),
    });

    renderRoute(true);
    expect(screen.getByText('Home Page')).toBeInTheDocument();
  });

  it('renders protected content for admin users', () => {
    mockedUseAuth.mockReturnValue({
      user: { userId: 2, email: 'admin@example.com', firstName: 'Admin', role: 'ADMIN' },
      isInitializing: false,
      isAuthenticated: true,
      isAdmin: true,
      login: vi.fn(),
      register: vi.fn(),
      logout: vi.fn(),
      refreshSession: vi.fn(),
    });

    renderRoute(true);
    expect(screen.getByText('Protected Content')).toBeInTheDocument();
  });
});
