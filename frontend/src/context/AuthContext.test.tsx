import { describe, expect, it, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { AuthProvider, useAuth } from '@/context/AuthContext';

const {
  loginMock,
  registerMock,
  logoutMock,
  verifySessionMock,
  getStoredUserMock,
} = vi.hoisted(() => ({
  loginMock: vi.fn(),
  registerMock: vi.fn(),
  logoutMock: vi.fn(),
  verifySessionMock: vi.fn(),
  getStoredUserMock: vi.fn(),
}));

vi.mock('@/api/client', () => ({
  login: loginMock,
  register: registerMock,
  logout: logoutMock,
  verifySession: verifySessionMock,
  getStoredUser: getStoredUserMock,
}));

function AuthProbe() {
  const auth = useAuth();
  return (
    <div>
      <span data-testid="auth-state">{auth.isAuthenticated ? 'yes' : 'no'}</span>
      <span data-testid="role">{auth.user?.role ?? 'none'}</span>
      <button onClick={() => auth.login('a@b.com', 'secret')}>login</button>
      <button onClick={() => auth.logout()}>logout</button>
    </div>
  );
}

describe('AuthContext', () => {
  it('hydrates auth state from verifySession on mount', async () => {
    getStoredUserMock.mockReturnValue(null);
    verifySessionMock.mockResolvedValue({
      userId: 1,
      email: 'user@example.com',
      firstName: 'User',
      role: 'USER',
    });

    render(
      <AuthProvider>
        <AuthProbe />
      </AuthProvider>,
    );

    await waitFor(() => expect(screen.getByTestId('auth-state')).toHaveTextContent('yes'));
    expect(screen.getByTestId('role')).toHaveTextContent('USER');
  });

  it('updates state on login and logout actions', async () => {
    getStoredUserMock.mockReset();
    getStoredUserMock
      .mockReturnValueOnce(null)
      .mockReturnValue({
        userId: 2,
        email: 'admin@example.com',
        firstName: 'Admin',
        role: 'ADMIN',
      });

    verifySessionMock.mockResolvedValue(null);
    loginMock.mockResolvedValue({
      token: null,
      userId: 2,
      email: 'admin@example.com',
      firstName: 'Admin',
      lastName: 'User',
      role: 'ADMIN',
    });
    logoutMock.mockResolvedValue(undefined);

    render(
      <AuthProvider>
        <AuthProbe />
      </AuthProvider>,
    );

    await waitFor(() => expect(screen.getByTestId('auth-state')).toHaveTextContent('no'));

    await userEvent.click(screen.getByText('login'));
    await waitFor(() => expect(screen.getByTestId('role')).toHaveTextContent('ADMIN'));

    await userEvent.click(screen.getByText('logout'));
    await waitFor(() => expect(screen.getByTestId('auth-state')).toHaveTextContent('no'));
  });
});
