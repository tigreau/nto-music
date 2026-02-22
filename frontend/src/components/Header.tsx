import { useAuth } from '@/context/AuthContext';
import { useCart } from '@/context/CartContext';
import { formatUserDisplayName } from '@/lib/formatters';
import { HeaderView } from '@/components/HeaderView';
import { useLocation, useNavigate } from 'react-router-dom';
import { useEffect, useMemo, useState } from 'react';

export function Header() {
  const { user, isAuthenticated, isAdmin, logout } = useAuth();
  const { cartTotalItems } = useCart();
  const location = useLocation();
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const showSearch = location.pathname !== '/login';

  const firstName = user?.firstName || 'Profile';
  const lastName = user?.lastName || '';
  const urlSearchTerm = useMemo(
    () => new URLSearchParams(location.search).get('q') ?? '',
    [location.search],
  );

  useEffect(() => {
    setSearchTerm(urlSearchTerm);
  }, [urlSearchTerm]);

  const handleSearchSubmit = () => {
    const params = new URLSearchParams(location.search);
    const normalized = searchTerm.trim();
    if (normalized) {
      params.set('q', normalized);
    } else {
      params.delete('q');
    }
    params.set('page', '0');
    const queryString = params.toString();
    navigate(`/${queryString ? `?${queryString}` : ''}`);
  };

  return (
    <HeaderView
      isAuthenticated={isAuthenticated}
      isAdmin={isAdmin}
      cartTotalItems={cartTotalItems}
      displayName={formatUserDisplayName(firstName, lastName)}
      fullName={`${firstName} ${lastName}`.trim()}
      onLogout={logout}
      showSearch={showSearch}
      searchTerm={searchTerm}
      onSearchTermChange={setSearchTerm}
      onSearchSubmit={handleSearchSubmit}
    />
  );
}
