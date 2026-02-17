import { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import CartPage from './pages/CartPage';
import CheckoutPage from './pages/CheckoutPage';
import AdminPage from './pages/AdminPage';
import UserProfilePage from './pages/UserProfilePage';
import LoginPage from './pages/LoginPage';
import ProtectedRoute from './components/ProtectedRoute';
import { Header } from "./components/Header";
import { Footer } from "./components/Footer";
import { CartProvider } from "./context/CartContext";
import { Toaster } from "sonner";
import { getStoredUser, logout as apiLogout, verifySession } from "./api/client";

function App() {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isAdmin, setIsAdmin] = useState(false);

    useEffect(() => {
        // Single source of truth: backend validation
        verifySession().then(user => {
            setIsAuthenticated(!!user);
            setIsAdmin(user?.role === 'ADMIN');
        });
    }, []);

    const handleLogout = async () => {
        await apiLogout();
        setIsAuthenticated(false);
        setIsAdmin(false);
    };

    return (
        <Router>
            <CartProvider key={isAuthenticated ? 'auth' : 'guest'}>
                <div className="min-h-screen flex flex-col bg-background">
                    <Header
                        isAuthenticated={isAuthenticated}
                        isAdmin={isAdmin}
                        onLogout={handleLogout}
                    />
                    <main className="flex-1">
                        <Routes>
                            <Route path="/" element={
                                <ProtectedRoute>
                                    <HomePage isAdmin={isAdmin} />
                                </ProtectedRoute>
                            } />
                            <Route path="/cart" element={
                                <ProtectedRoute>
                                    <CartPage />
                                </ProtectedRoute>
                            } />
                            <Route path="/checkout" element={
                                <ProtectedRoute>
                                    <CheckoutPage />
                                </ProtectedRoute>
                            } />
                            <Route path="/admin" element={
                                <ProtectedRoute adminOnly={true}>
                                    <AdminPage />
                                </ProtectedRoute>
                            } />
                            <Route path="/user-profile" element={
                                <ProtectedRoute>
                                    <UserProfilePage />
                                </ProtectedRoute>
                            } />
                            <Route path="/login"
                                element={<LoginPage setIsAuthenticated={setIsAuthenticated} setIsAdmin={setIsAdmin} />} />
                        </Routes>
                    </main>
                    <Footer />
                    <Toaster
                        position="bottom-right"
                        toastOptions={{
                            style: {
                                background: '#eee8d5',
                                color: '#002b36',
                                border: '1px solid #93a1a1',
                                fontFamily: 'Inter, system-ui, sans-serif',
                            },
                            classNames: {
                                success: 'toast-success',
                                error: 'toast-error',
                            },
                        }}
                    />
                </div>
            </CartProvider>
        </Router>
    );
}

export default App;
