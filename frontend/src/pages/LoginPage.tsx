import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { login, register } from "@/api/client";

interface LoginPageProps {
    setIsAuthenticated: (value: boolean) => void;
    setIsAdmin: (value: boolean) => void;
}

const LoginPage = ({ setIsAuthenticated, setIsAdmin }: LoginPageProps) => {
    const navigate = useNavigate();
    const [isRegisterMode, setIsRegisterMode] = useState(false);
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError("");
        setLoading(true);

        try {
            let res;
            if (isRegisterMode) {
                res = await register(firstName, lastName, email, password);
            } else {
                res = await login(email, password);
            }

            setIsAuthenticated(true);
            setIsAdmin(res.role === "ADMIN");

            if (res.role === "ADMIN") {
                navigate("/admin");
            } else {
                navigate("/");
            }
        } catch (err: unknown) {
            setError(err instanceof Error ? err.message : "Something went wrong");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-[80vh] flex items-center justify-center px-4">
            <div className="w-full max-w-md">
                <div className="bg-card rounded-2xl border border-border p-8 shadow-lg">
                    <div className="text-center mb-8">
                        <h1 className="font-[family-name:var(--font-display)] text-3xl text-foreground mb-2">
                            {isRegisterMode ? "Create Account" : "Welcome Back"}
                        </h1>
                        <p className="text-muted-foreground">
                            {isRegisterMode
                                ? "Sign up to start shopping"
                                : "Sign in to your account"}
                        </p>
                    </div>

                    {error && (
                        <div className="mb-4 p-3 rounded-lg bg-red-500/10 border border-red-500/30 text-red-400 text-sm text-center">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-4">
                        {isRegisterMode && (
                            <div className="grid grid-cols-2 gap-3">
                                <Input
                                    type="text"
                                    placeholder="First name"
                                    value={firstName}
                                    onChange={(e) => setFirstName(e.target.value)}
                                    required
                                    className="bg-background/50 border-border"
                                />
                                <Input
                                    type="text"
                                    placeholder="Last name"
                                    value={lastName}
                                    onChange={(e) => setLastName(e.target.value)}
                                    required
                                    className="bg-background/50 border-border"
                                />
                            </div>
                        )}

                        <Input
                            type="email"
                            placeholder="Email address"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            className="bg-background/50 border-border"
                        />

                        <Input
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            minLength={6}
                            className="bg-background/50 border-border"
                        />

                        <Button
                            type="submit"
                            size="lg"
                            className="w-full bg-primary text-primary-foreground hover:bg-primary/90"
                            disabled={loading}
                        >
                            {loading
                                ? "Please wait..."
                                : isRegisterMode
                                    ? "Create Account"
                                    : "Sign In"}
                        </Button>
                    </form>

                    <div className="mt-6 text-center">
                        <button
                            type="button"
                            className="text-sm text-muted-foreground hover:text-foreground transition-colors"
                            onClick={() => {
                                setIsRegisterMode(!isRegisterMode);
                                setError("");
                            }}
                        >
                            {isRegisterMode
                                ? "Already have an account? Sign in"
                                : "Don't have an account? Sign up"}
                        </button>
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
