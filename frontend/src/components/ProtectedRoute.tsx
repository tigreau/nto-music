import { Navigate, useLocation } from 'react-router-dom';
import { ReactNode } from 'react';
import { useAuth } from '@/context/AuthContext';
import { LoadingState } from '@/components/state/LoadingState';

interface ProtectedRouteProps {
    children: ReactNode;
    adminOnly?: boolean;
}

const ProtectedRoute = ({ children, adminOnly = false }: ProtectedRouteProps) => {
    const { user, isInitializing } = useAuth();
    const location = useLocation();

    if (isInitializing) {
        return <LoadingState message="Checking session..." className="min-h-[40vh]" />;
    }

    if (!user) {
        return <Navigate to="/login" state={{ from: location }} />;
    }

    if (adminOnly && user?.role !== 'ADMIN') {
        return <Navigate to="/" />;
    }

    return <>{children}</>;
};

export default ProtectedRoute;
