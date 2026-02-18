import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { EmptyState } from '@/components/state/EmptyState';
import { AsyncPageState } from '@/components/state/AsyncPageState';
import { useCart } from '@/context/CartContext';
import { getApiErrorPolicy } from '@/lib/apiError';
import { getCategoryImage } from '@/lib/categoryUtils';
import { CreditCard, ShoppingBag, MapPin, ChevronLeft, CheckCircle } from 'lucide-react';
import { useCheckout } from '@/hooks/useApi';
import { CheckoutResult } from '@/types';
import { useMutationFeedback } from '@/hooks/useMutationFeedback';

const TAX_RATE = 0.21;
const SHIPPING_FEE = 5.99;

const CheckoutPage = () => {
    const { cartItems, refreshCart, isLoading, cartError, clearCartError } = useCart();
    const navigate = useNavigate();
    const checkoutMutation = useCheckout();
    const runWithFeedback = useMutationFeedback();
    const isMountedRef = useRef(true);

    const [address, setAddress] = useState({
        street: '',
        number: '',
        postalCode: '',
        city: '',
        country: '',
    });
    const [paymentMethod, setPaymentMethod] = useState('credit_card');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState('');
    const [orderResult, setOrderResult] = useState<CheckoutResult | null>(null);

    useEffect(() => {
        isMountedRef.current = true;
        refreshCart();
        return () => {
            isMountedRef.current = false;
        };
    }, [refreshCart]);

    const subtotal = cartItems.reduce((sum, item) => {
        return sum + (item.product.price * item.quantity);
    }, 0);

    const tax = subtotal * TAX_RATE;
    const total = subtotal + tax + SHIPPING_FEE;

    const handleAddressChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setAddress({ ...address, [e.target.name]: e.target.value });
    };

    const isFormValid = address.street && address.number && address.postalCode && address.city && address.country;

    const handleSubmit = async () => {
        if (!isFormValid) return;
        setIsSubmitting(true);
        setError('');

        const result = await runWithFeedback(
            () => checkoutMutation.mutateAsync({
                    paymentMethod,
                    street: address.street,
                    number: address.number,
                    postalCode: address.postalCode,
                    city: address.city,
                    country: address.country,
            }),
            {
                context: 'checkout.submit',
                onError: (err) => {
                    if (isMountedRef.current) {
                        setError(getApiErrorPolicy(err).message);
                    }
                },
            },
        );

        if (result && isMountedRef.current) {
            setOrderResult(result);
            void refreshCart();
        }
        if (isMountedRef.current) {
            setIsSubmitting(false);
        }
    };

    // Success screen
    if (orderResult) {
        return (
            <div className="min-h-screen bg-background">
                <div className="bg-[#073642] border-b-2 border-[#002b36]">
                    <div className="container mx-auto px-4 py-6">
                        <div className="flex items-center gap-3">
                            <CheckCircle className="w-8 h-8 text-[#859900]" />
                            <h1 className="font-[family-name:var(--font-display)] text-4xl text-[#fdf6e3] tracking-tight">
                                ORDER CONFIRMED
                            </h1>
                        </div>
                    </div>
                </div>
                <div className="container mx-auto px-4 py-8">
                    <div className="max-w-lg mx-auto">
                        <div className="bg-card rounded-xl border border-border p-8 text-center">
                            <CheckCircle className="w-16 h-16 text-[#859900] mx-auto mb-4" />
                            <h2 className="text-2xl font-bold text-foreground mb-2">Thank you for your order!</h2>
                            <p className="text-muted-foreground mb-6">
                                Your order #{orderResult.orderId} has been placed successfully.
                            </p>
                            <div className="bg-muted rounded-lg p-4 mb-6 text-left space-y-2">
                                <div className="flex justify-between text-sm">
                                    <span className="text-muted-foreground">Order ID</span>
                                    <span className="font-medium text-foreground">#{orderResult.orderId}</span>
                                </div>
                                <div className="flex justify-between text-sm">
                                    <span className="text-muted-foreground">Total</span>
                                    <span className="font-medium text-foreground">{orderResult.totalAmount.toFixed(2)} EUR</span>
                                </div>
                                <div className="flex justify-between text-sm">
                                    <span className="text-muted-foreground">Payment</span>
                                    <span className="font-medium text-foreground">{orderResult.paymentStatus}</span>
                                </div>
                                <div className="flex justify-between text-sm">
                                    <span className="text-muted-foreground">Transaction</span>
                                    <span className="font-mono text-xs text-foreground">{orderResult.transactionId}</span>
                                </div>
                            </div>
                            <Button onClick={() => navigate('/')} size="lg">
                                Continue Shopping
                            </Button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-background">
            {/* Header */}
            <div className="bg-[#073642] border-b-2 border-[#002b36]">
                <div className="container mx-auto px-4 py-6">
                    <div className="flex items-center gap-3">
                        <CreditCard className="w-8 h-8 text-[#268bd2]" />
                        <h1 className="font-[family-name:var(--font-display)] text-4xl text-[#fdf6e3] tracking-tight">
                            CHECKOUT
                        </h1>
                    </div>
                </div>
            </div>

            <div className="container mx-auto px-4 py-8">
                <AsyncPageState
                    isLoading={isLoading}
                    isError={!!cartError}
                    errorMessage={cartError ? getApiErrorPolicy(cartError).message : undefined}
                    onRetry={() => {
                        clearCartError();
                        refreshCart();
                    }}
                    loadingMessage="Loading checkout..."
                    loadingClassName="min-h-[40vh]"
                    empty={cartItems.length === 0}
                    emptyState={
                        <div className="max-w-lg mx-auto">
                            <EmptyState
                                title="Your cart is empty"
                                description="Add items to your cart before checking out."
                                icon={<ShoppingBag className="w-16 h-16 text-muted-foreground mx-auto mb-4" />}
                                action={<Button onClick={() => navigate('/')}>Browse Products</Button>}
                            />
                        </div>
                    }
                >
                <div className="max-w-5xl mx-auto">
                    {/* Back to cart */}
                    <button
                        onClick={() => navigate('/cart')}
                        className="flex items-center gap-1 text-sm text-muted-foreground hover:text-foreground mb-6 transition-colors"
                    >
                        <ChevronLeft className="w-4 h-4" />
                        Back to cart
                    </button>

                    <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                        {/* Left column: forms */}
                        <div className="lg:col-span-2 space-y-6">
                            {/* Shipping Address */}
                            <div className="bg-card rounded-xl border border-border p-6">
                                <h2 className="font-semibold text-foreground mb-4 flex items-center gap-2">
                                    <MapPin className="w-5 h-5 text-[#268bd2]" />
                                    Shipping Address
                                </h2>
                                <div className="space-y-4">
                                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                                        <div className="sm:col-span-2 space-y-2">
                                            <label className="text-sm font-medium text-foreground">Street</label>
                                            <Input
                                                name="street"
                                                value={address.street}
                                                onChange={handleAddressChange}
                                                placeholder="Main Street"
                                            />
                                        </div>
                                        <div className="space-y-2">
                                            <label className="text-sm font-medium text-foreground">Number</label>
                                            <Input
                                                name="number"
                                                value={address.number}
                                                onChange={handleAddressChange}
                                                placeholder="42A"
                                            />
                                        </div>
                                    </div>
                                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                                        <div className="space-y-2">
                                            <label className="text-sm font-medium text-foreground">Postal Code</label>
                                            <Input
                                                name="postalCode"
                                                value={address.postalCode}
                                                onChange={handleAddressChange}
                                                placeholder="10001"
                                            />
                                        </div>
                                        <div className="space-y-2">
                                            <label className="text-sm font-medium text-foreground">City</label>
                                            <Input
                                                name="city"
                                                value={address.city}
                                                onChange={handleAddressChange}
                                                placeholder="Amsterdam"
                                            />
                                        </div>
                                        <div className="space-y-2">
                                            <label className="text-sm font-medium text-foreground">Country</label>
                                            <Input
                                                name="country"
                                                value={address.country}
                                                onChange={handleAddressChange}
                                                placeholder="Netherlands"
                                            />
                                        </div>
                                    </div>
                                </div>
                            </div>

                            {/* Payment Method */}
                            <div className="bg-card rounded-xl border border-border p-6">
                                <h2 className="font-semibold text-foreground mb-4 flex items-center gap-2">
                                    <CreditCard className="w-5 h-5 text-[#268bd2]" />
                                    Payment Method
                                </h2>
                                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                                    <button
                                        onClick={() => setPaymentMethod('credit_card')}
                                        className={`p-4 rounded-lg border-2 text-left transition-colors ${
                                            paymentMethod === 'credit_card'
                                                ? 'border-[#268bd2] bg-[#268bd2]/10'
                                                : 'border-border hover:border-muted-foreground'
                                        }`}
                                    >
                                        <div className="font-medium text-foreground">Credit Card</div>
                                        <div className="text-sm text-muted-foreground">Pay with Visa, Mastercard, etc.</div>
                                    </button>
                                    <button
                                        onClick={() => setPaymentMethod('paypal')}
                                        className={`p-4 rounded-lg border-2 text-left transition-colors ${
                                            paymentMethod === 'paypal'
                                                ? 'border-[#268bd2] bg-[#268bd2]/10'
                                                : 'border-border hover:border-muted-foreground'
                                        }`}
                                    >
                                        <div className="font-medium text-foreground">PayPal</div>
                                        <div className="text-sm text-muted-foreground">Pay with your PayPal account</div>
                                    </button>
                                </div>
                            </div>
                        </div>

                        {/* Right column: order summary */}
                        <div className="space-y-6">
                            {/* Items */}
                            <div className="bg-card rounded-xl border border-border p-6">
                                <h2 className="font-semibold text-foreground mb-4">
                                    Order Summary ({cartItems.length} {cartItems.length === 1 ? 'item' : 'items'})
                                </h2>
                                <div className="space-y-3 mb-4">
                                    {cartItems.map(cartItem => {
                                        const product = cartItem.product;
                                        const imageUrl = product.thumbnailUrl || getCategoryImage(product.category?.name);
                                        return (
                                            <div key={cartItem.id} className="flex items-center gap-3">
                                                <div className="w-12 h-12 bg-muted rounded-md flex-shrink-0 overflow-hidden border border-[#93a1a1]">
                                                    {imageUrl ? (
                                                        <img src={imageUrl} alt={product.name} className="w-full h-full object-cover" />
                                                    ) : (
                                                        <div className="w-full h-full bg-gradient-to-br from-muted-foreground/20 to-muted-foreground/10" />
                                                    )}
                                                </div>
                                                <div className="flex-1 min-w-0">
                                                    <p className="text-sm font-medium text-foreground truncate">{product.name}</p>
                                                    <p className="text-xs text-muted-foreground">Qty: {cartItem.quantity}</p>
                                                </div>
                                                <p className="text-sm font-medium text-foreground">
                                                    {(product.price * cartItem.quantity).toFixed(2)}
                                                </p>
                                            </div>
                                        );
                                    })}
                                </div>

                                <div className="border-t border-border pt-4 space-y-2">
                                    <div className="flex justify-between text-sm">
                                        <span className="text-muted-foreground">Subtotal</span>
                                        <span className="text-foreground">{subtotal.toFixed(2)} EUR</span>
                                    </div>
                                    <div className="flex justify-between text-sm">
                                        <span className="text-muted-foreground">Tax (21%)</span>
                                        <span className="text-foreground">{tax.toFixed(2)} EUR</span>
                                    </div>
                                    <div className="flex justify-between text-sm">
                                        <span className="text-muted-foreground">Shipping</span>
                                        <span className="text-foreground">{SHIPPING_FEE.toFixed(2)} EUR</span>
                                    </div>
                                </div>

                                <div className="border-t border-border pt-4 mt-4">
                                    <div className="flex justify-between">
                                        <span className="font-semibold text-foreground">Total</span>
                                        <span className="font-bold text-xl text-primary">{total.toFixed(2)} EUR</span>
                                    </div>
                                </div>
                            </div>

                            {/* Place Order Button */}
                            {error && (
                                <div className="bg-destructive/10 border border-destructive/30 rounded-lg p-3 text-sm text-destructive">
                                    {error}
                                </div>
                            )}
                            <Button
                                className="w-full"
                                size="lg"
                                onClick={handleSubmit}
                                disabled={!isFormValid || isSubmitting}
                            >
                                {isSubmitting ? 'Processing...' : `Place Order - ${total.toFixed(2)} EUR`}
                            </Button>
                        </div>
                    </div>
                </div>
                </AsyncPageState>
            </div>
        </div>
    );
};

export default CheckoutPage;
