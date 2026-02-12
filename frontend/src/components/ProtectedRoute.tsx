import { Navigate, useLocation } from 'react-router-dom';
import { ReactNode } from 'react';
import { getToken, getStoredUser } from '@/api/client';

interface ProtectedRouteProps {
    children: ReactNode;
    adminOnly?: boolean;
}

const ProtectedRoute = ({ children, adminOnly = false }: ProtectedRouteProps) => {
    const token = getToken();
    const user = getStoredUser();
    const location = useLocation();

    if (!token) {
        return <Navigate to="/login" state={{ from: location }} />;
    }

    if (adminOnly && user?.role !== 'ADMIN') {
        return <Navigate to="/" />;
    }

    return <>{children}</>;
};

export default ProtectedRoute;
