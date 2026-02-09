import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { User, Shield } from "lucide-react";

interface LoginPageProps {
    setIsAuthenticated: (value: boolean) => void;
    setIsAdmin: (value: boolean) => void;
}

const LoginPage = ({ setIsAuthenticated, setIsAdmin }: LoginPageProps) => {
    const navigate = useNavigate();

    const handleCustomerLogin = () => {
        // Basic auth using using session storage
        // End points related to user id will be called with 1
        // because for now there is a single user
        sessionStorage.setItem('isAuthenticated', 'true');
        sessionStorage.setItem('userId', '1'); // Assuming John Doe has ID 1
        setIsAuthenticated(true);
        setIsAdmin(false);
        navigate('/');
    };

    const handleAdminLogin = () => {
        sessionStorage.setItem('isAuthenticated', 'true');
        sessionStorage.setItem('isAdmin', 'true');
        setIsAuthenticated(true);
        setIsAdmin(true);
        navigate('/admin');
    };

    return (
        <div className="min-h-[80vh] flex items-center justify-center px-4">
            <div className="w-full max-w-md">
                <div className="bg-card rounded-2xl border border-border p-8 shadow-lg">
                    <div className="text-center mb-8">
                        <h1 className="font-[family-name:var(--font-display)] text-3xl text-foreground mb-2">
                            Welcome Back
                        </h1>
                        <p className="text-muted-foreground">
                            Choose how you'd like to sign in
                        </p>
                    </div>

                    <div className="space-y-4">
                        <Button
                            onClick={handleCustomerLogin}
                            size="lg"
                            className="w-full bg-primary text-primary-foreground hover:bg-primary/90"
                        >
                            <User className="w-5 h-5 mr-2" />
                            Log in as Customer
                        </Button>

                        <Button
                            onClick={handleAdminLogin}
                            size="lg"
                            variant="outline"
                            className="w-full"
                        >
                            <Shield className="w-5 h-5 mr-2" />
                            Log in as Admin
                        </Button>
                    </div>

                    <p className="text-center text-xs text-muted-foreground mt-6">
                        By signing in, you agree to our Terms of Service and Privacy Policy.
                    </p>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;
