import { useAuth } from '@/context/AuthContext';
import { useCart } from '@/context/CartContext';
import { formatUserDisplayName } from '@/lib/formatters';
import { HeaderView } from '@/components/HeaderView';

export function Header() {
  const { user, isAuthenticated, isAdmin, logout } = useAuth();
  const { cartTotalItems } = useCart();

  const firstName = user?.firstName || 'Profile';
  const lastName = user?.lastName || '';

  return (
    <HeaderView
      isAuthenticated={isAuthenticated}
      isAdmin={isAdmin}
      cartTotalItems={cartTotalItems}
      displayName={formatUserDisplayName(firstName, lastName)}
      fullName={`${firstName} ${lastName}`.trim()}
      onLogout={logout}
    />
  );
}
