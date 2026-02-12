import { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import CartPage from './pages/CartPage';
import AdminPage from './pages/AdminPage';
import UserProfilePage from './pages/UserProfilePage';
import LoginPage from './pages/LoginPage';
import ProtectedRoute from './components/ProtectedRoute';
import Notifications from "./components/Notifications";
import { Header } from "./components/Header";
import { Footer } from "./components/Footer";
import { CartProvider } from "./context/CartContext";
import { getToken, getStoredUser, clearToken } from "./api/client";

function App() {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isAdmin, setIsAdmin] = useState(false);

    useEffect(() => {
        const token = getToken();
        const user = getStoredUser();
        setIsAuthenticated(!!token);
        setIsAdmin(user?.role === 'ADMIN');
    }, []);

    const handleLogout = () => {
        clearToken();
        setIsAuthenticated(false);
        setIsAdmin(false);
    };

    return (
        <Router>
            <CartProvider>
                <div className="min-h-screen flex flex-col bg-background">
                    <Notifications />
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
                </div>
            </CartProvider>
        </Router>
    );
}

export default App;
