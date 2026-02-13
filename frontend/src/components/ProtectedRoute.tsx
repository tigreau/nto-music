import { Navigate, useLocation } from 'react-router-dom';
import { ReactNode } from 'react';
import { getStoredUser } from '@/api/client';

interface ProtectedRouteProps {
    children: ReactNode;
    adminOnly?: boolean;
}

const ProtectedRoute = ({ children, adminOnly = false }: ProtectedRouteProps) => {
    const user = getStoredUser();
    const location = useLocation();

    if (!user) {
        return <Navigate to="/login" state={{ from: location }} />;
    }

    if (adminOnly && user?.role !== 'ADMIN') {
        return <Navigate to="/" />;
    }

    return <>{children}</>;
};

export default ProtectedRoute;
