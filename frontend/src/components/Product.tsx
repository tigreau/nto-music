import { Button } from "@/components/ui/button";
import { ShoppingCart } from "lucide-react";

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
    const addToCart = (e: React.MouseEvent, productId: number, availableQuantity: number | undefined) => {
        e.stopPropagation(); // Prevents the click from reaching the parent div

        const quantity = parseInt(prompt('Enter quantity:', '1') || '0', 10);

        if (isNaN(quantity) || quantity <= 0) {
            alert('Please enter a valid quantity.');
            return;
        }

        if (availableQuantity && quantity > availableQuantity) {
            alert(`Only ${availableQuantity} items available.`);
            return;
        }

        fetch(`/api/carts/my/products/${productId}?quantity=${quantity}`, {
            method: 'POST',
            credentials: 'include'
        })
            .then(response => {
                if (response.ok) {
                    alert(`Added ${quantity} items to the cart.`);
                } else {
                    alert('Failed to add items to the cart.');
                }
            })
            .catch(error => {
                console.error('Error adding items to the cart:', error);
            });
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
                    onClick={(e) => addToCart(e, product.id, product.quantityAvailable)}
                >
                    <ShoppingCart className="w-4 h-4 mr-2" />
                    Add to Cart
                </Button>
            )}
        </div>
    );
};

export default Product;
