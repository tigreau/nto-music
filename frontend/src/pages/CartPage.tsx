import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { useCart } from "@/context/CartContext";
import { Trash2, ShoppingBag, ArrowRight } from 'lucide-react';
import { getCategoryImage } from "@/lib/categoryUtils";
import { toast } from 'sonner';

const CartPage = () => {
    const { cartItems, refreshCart } = useCart();
    const navigate = useNavigate();

    // Refresh cart on mount to ensure prices are up to date
    useEffect(() => {
        refreshCart();
    }, [refreshCart]);

    // Calculate total price
    const totalPrice = cartItems.reduce((sum, item) => {
        // Handle potential data structure mismatch if products property name varies slightly
        // Context interface says `products` but API returns `product` for details?
        // Let's assume the data flowing through is consistent with what the API returns.
        // If context just passes through the API response, it should be fine.
        const product = (item as any).product || (item as any).products; // Fallback
        return sum + (product.price * item.quantity);
    }, 0);

    const deleteCartItem = (cartItemId: number) => {
        fetch(`/api/carts/details/${cartItemId}`, {
            method: 'DELETE',
            credentials: 'include'
        })
            .then(response => {
                if (response.ok) {
                    toast.success('Item removed from cart');
                    refreshCart();
                } else {
                    toast.error('Failed to remove item');
                }
            })
            .catch(error => console.error('Error deleting cart item:', error));
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

                    {cartItems.length === 0 ? (
                        <div className="bg-card rounded-xl border border-border p-12 text-center">
                            <ShoppingBag className="w-16 h-16 text-muted-foreground mx-auto mb-4" />
                            <h2 className="text-xl font-semibold text-foreground mb-2">Your cart is empty</h2>
                            <p className="text-muted-foreground mb-6">Start shopping to add items to your cart.</p>
                            <Button asChild>
                                <a href="/">Browse Products</a>
                            </Button>
                        </div>
                    ) : (
                        <div className="space-y-4">
                            {/* Cart Items */}
                            <div className="bg-card rounded-xl border border-border overflow-hidden">
                                <div className="divide-y divide-border">
                                    {cartItems.map(cartItem => {
                                        const product = (cartItem as any).product || (cartItem as any).products;
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
                                                    onClick={() => deleteCartItem(cartItem.id)}
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
                    )}
                </div>
            </div>
        </div>
    );
};

export default CartPage;
