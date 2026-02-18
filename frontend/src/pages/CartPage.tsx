import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { EmptyState } from '@/components/state/EmptyState';
import { AsyncPageState } from '@/components/state/AsyncPageState';
import { useCart } from "@/context/CartContext";
import { Trash2, ShoppingBag, ArrowRight } from 'lucide-react';
import { getCategoryImage } from "@/lib/categoryUtils";
import { useDeleteCartItem } from '@/hooks/useApi';
import { getApiErrorPolicy } from '@/lib/apiError';
import { useMutationFeedback } from '@/hooks/useMutationFeedback';

const CartPage = () => {
    const { cartItems, refreshCart, isLoading, cartError, clearCartError } = useCart();
    const navigate = useNavigate();
    const deleteCartItemMutation = useDeleteCartItem();
    const runWithFeedback = useMutationFeedback();

    // Refresh cart on mount to ensure prices are up to date
    useEffect(() => {
        refreshCart();
    }, [refreshCart]);

    // Calculate total price
    const totalPrice = cartItems.reduce((sum, item) => {
        return sum + (item.product.price * item.quantity);
    }, 0);

    const deleteItem = (cartItemId: number) => {
        void runWithFeedback(
            () => deleteCartItemMutation.mutateAsync(cartItemId),
            {
                context: 'cart.deleteItem',
                successMessage: 'Item removed from cart',
                onSuccess: () => {
                    void refreshCart();
                },
            },
        );
    };

    const handleCheckout = () => {
        navigate('/checkout');
    };

    return (
        <div className="min-h-screen bg-background">
            {/* Header Section */}
            <div className="bg-[#073642] border-b-2 border-[#002b36]">
                <div className="container mx-auto px-4 py-6">
                    <div className="flex items-center gap-3">
                        <ShoppingBag className="w-8 h-8 text-[#268bd2]" />
                        <h1 className="font-[family-name:var(--font-display)] text-4xl text-[#fdf6e3] tracking-tight">
                            SHOPPING CART
                        </h1>
                    </div>
                </div>
            </div>

            <div className="container mx-auto px-4 py-8">
                <div className="max-w-4xl mx-auto">
                    <AsyncPageState
                        isLoading={isLoading}
                        isError={!!cartError}
                        errorMessage={cartError ? getApiErrorPolicy(cartError).message : undefined}
                        onRetry={() => {
                            clearCartError();
                            refreshCart();
                        }}
                        loadingMessage="Loading cart..."
                        loadingClassName="min-h-[40vh]"
                        empty={cartItems.length === 0}
                        emptyState={
                            <EmptyState
                                title="Your cart is empty"
                                description="Start shopping to add items to your cart."
                                icon={<ShoppingBag className="w-16 h-16 text-muted-foreground mx-auto mb-4" />}
                                action={
                                    <Button asChild>
                                        <a href="/">Browse Products</a>
                                    </Button>
                                }
                            />
                        }
                    >
                        <div className="space-y-4">
                            {/* Cart Items */}
                            <div className="bg-card rounded-xl border border-border overflow-hidden">
                                <div className="divide-y divide-border">
                                    {cartItems.map(cartItem => {
                                        const product = cartItem.product;
                                        const imageUrl = product.thumbnailUrl || getCategoryImage(product.category?.name);

                                        return (
                                            <div key={cartItem.id} className="p-4 flex items-center gap-4">
                                                {/* Product Image */}
                                                <div className="w-20 h-20 bg-muted rounded-lg flex items-center justify-center flex-shrink-0 overflow-hidden border border-[#93a1a1]">
                                                    {imageUrl ? (
                                                        <img
                                                            src={imageUrl}
                                                            alt={product.name}
                                                            className="w-full h-full object-cover"
                                                        />
                                                    ) : (
                                                        <div className="w-12 h-12 bg-gradient-to-br from-muted-foreground/20 to-muted-foreground/10 rounded-md" />
                                                    )}
                                                </div>

                                                {/* Product Info */}
                                                <div className="flex-1 min-w-0">
                                                    <h3 className="font-medium text-foreground truncate">
                                                        {product.name}
                                                    </h3>
                                                    <p className="text-sm text-muted-foreground">
                                                        Quantity: {cartItem.quantity}
                                                    </p>
                                                </div>

                                                {/* Price */}
                                                <div className="text-right">
                                                    <p className="font-semibold text-primary">
                                                        {(product.price * cartItem.quantity).toFixed(2)} EUR
                                                    </p>
                                                    <p className="text-xs text-muted-foreground">
                                                        {product.price.toFixed(2)} EUR each
                                                    </p>
                                                </div>

                                                {/* Delete Button */}
                                                <Button
                                                    variant="ghost"
                                                    size="icon"
                                                    className="text-destructive hover:text-destructive hover:bg-destructive/10"
                                                    onClick={() => deleteItem(cartItem.id)}
                                                >
                                                    <Trash2 className="w-4 h-4" />
                                                </Button>
                                            </div>
                                        );
                                    })}
                                </div>
                            </div>

                            {/* Order Summary */}
                            <div className="bg-card rounded-xl border border-border p-6">
                                <h2 className="font-semibold text-foreground mb-4">Order Summary</h2>
                                <div className="space-y-2 mb-4">
                                    <div className="flex justify-between text-sm">
                                        <span className="text-muted-foreground">Subtotal ({cartItems.length} items)</span>
                                        <span className="text-foreground">{totalPrice.toFixed(2)} EUR</span>
                                    </div>
                                    <div className="flex justify-between text-sm">
                                        <span className="text-muted-foreground">Shipping</span>
                                        <span className="text-emerald-600">Free</span>
                                    </div>
                                </div>
                                <div className="border-t border-border pt-4 mb-6">
                                    <div className="flex justify-between">
                                        <span className="font-semibold text-foreground">Total</span>
                                        <span className="font-bold text-xl text-primary">{totalPrice.toFixed(2)} EUR</span>
                                    </div>
                                </div>
                                <Button
                                    className="w-full"
                                    size="lg"
                                    onClick={handleCheckout}
                                >
                                    Proceed to Checkout
                                    <ArrowRight className="w-4 h-4 ml-2" />
                                </Button>
                            </div>
                        </div>
                    </AsyncPageState>
                </div>
            </div>
        </div>
    );
};

export default CartPage;
