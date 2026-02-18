import { Button } from "@/components/ui/button";
import { ShoppingCart } from "lucide-react";
import { toast } from "sonner";
import { useAddToCart } from '@/hooks/useApi';
import { getApiErrorPolicy } from '@/lib/apiError';

interface ProductData {
    id: number;
    name: string;
    price: number;
    quantityAvailable?: number;
}

interface ProductProps {
    product: ProductData;
    onProductClick: (product: ProductData) => void;
    isAdmin: boolean;
}

const Product = ({ product, onProductClick, isAdmin }: ProductProps) => {
    const addToCartMutation = useAddToCart();

    const handleAddToCart = (e: React.MouseEvent, productId: number) => {
        e.stopPropagation();

        addToCartMutation.mutate(
            { productId, quantity: 1 },
            {
                onSuccess: () => {
                toast.success('Added to cart');
                },
                onError: (error) => {
                    toast.error(getApiErrorPolicy(error).message);
                    console.error('Error adding item to cart:', error);
                },
            },
        );
    };

    return (
        <div
            className="bg-card rounded-lg border border-border p-4 hover:shadow-md transition-shadow cursor-pointer"
            onClick={() => onProductClick(product)}
        >
            {/* Product Image Placeholder */}
            <div className="aspect-square bg-muted/30 rounded-md mb-3 flex items-center justify-center">
                <div className="w-16 h-16 bg-gradient-to-br from-muted to-muted/50 rounded-lg" />
            </div>

            <h3 className="font-medium text-foreground mb-1 line-clamp-1">{product.name}</h3>
            <p className="text-lg font-bold text-primary mb-3">{product.price} EUR</p>

            {/* Admins don't have add to cart functionality */}
            {!isAdmin && (
                <Button
                    variant="outline"
                    size="sm"
                    className="w-full"
                    onClick={(e) => handleAddToCart(e, product.id)}
                >
                    <ShoppingCart className="w-4 h-4 mr-2" />
                    Add to Cart
                </Button>
            )}
        </div>
    );
};

export default Product;
