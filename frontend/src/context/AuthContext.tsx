import { createContext, ReactNode, useCallback, useContext, useEffect, useMemo, useRef, useState } from 'react';
import {
    AuthResponse,
    getStoredUser,
    login as loginRequest,
    logout as logoutRequest,
    register as registerRequest,
    verifySession,
} from '@/api/client';
import { AuthUser } from '@/types';

interface AuthContextType {
    user: AuthUser | null;
    isAuthenticated: boolean;
    isAdmin: boolean;
    isInitializing: boolean;
    login: (email: string, password: string) => Promise<AuthResponse>;
    register: (firstName: string, lastName: string, email: string, password: string) => Promise<AuthResponse>;
    logout: () => Promise<void>;
    refreshSession: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<AuthUser | null>(getStoredUser());
    const [isInitializing, setIsInitializing] = useState(true);
    const isMountedRef = useRef(true);

    const refreshSession = useCallback(async () => {
        const sessionUser = await verifySession();
        if (isMountedRef.current) {
            setUser(sessionUser);
        }
    }, []);

    useEffect(() => {
        isMountedRef.current = true;
        refreshSession()
            .finally(() => {
                if (isMountedRef.current) {
                    setIsInitializing(false);
                }
            });

        return () => {
            isMountedRef.current = false;
        };
    }, [refreshSession]);

    const login = async (email: string, password: string) => {
        const response = await loginRequest(email, password);
        if (isMountedRef.current) {
            setUser(getStoredUser());
        }
        return response;
    };

    const register = async (firstName: string, lastName: string, email: string, password: string) => {
        const response = await registerRequest(firstName, lastName, email, password);
        if (isMountedRef.current) {
            setUser(getStoredUser());
        }
        return response;
    };

    const logout = async () => {
        await logoutRequest();
        if (isMountedRef.current) {
            setUser(null);
        }
    };

    const value = useMemo<AuthContextType>(() => ({
        user,
        isAuthenticated: !!user,
        isAdmin: user?.role === 'ADMIN',
        isInitializing,
        login,
        register,
        logout,
        refreshSession,
    }), [isInitializing, refreshSession, user]);

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextType {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}
