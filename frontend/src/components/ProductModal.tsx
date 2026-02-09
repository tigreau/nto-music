import { Button } from "@/components/ui/button";
import { X, ShoppingCart } from "lucide-react";

interface Product {
    id: number;
    name: string;
    price: number;
    description?: string;
    quantityAvailable?: number;
    categoryName?: string;
}

interface ProductModalProps {
    product: Product;
    onClose: () => void;
    onAddToCart?: (productId: number, quantity: number) => void;
}

const ProductModal = ({ product, onClose, onAddToCart }: ProductModalProps) => {
    if (!product) return null;

    const handleAddToCart = () => {
        if (onAddToCart) {
            const quantity = parseInt(prompt('Enter quantity:', '1') || '0', 10);
            if (!isNaN(quantity) && quantity > 0) {
                onAddToCart(product.id, quantity);
                onClose();
            }
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
            {/* Backdrop */}
            <div 
                className="absolute inset-0 bg-black/50 backdrop-blur-sm"
                onClick={onClose}
            />
            
            {/* Modal */}
            <div className="relative bg-card rounded-xl border border-border shadow-2xl w-full max-w-lg mx-4 overflow-hidden">
                {/* Header */}
                <div className="flex items-center justify-between p-4 border-b border-border">
                    <h2 className="font-semibold text-lg text-foreground">{product.name}</h2>
                    <Button
                        variant="ghost"
                        size="icon"
                        onClick={onClose}
                        className="text-muted-foreground hover:text-foreground"
                    >
                        <X className="w-5 h-5" />
                    </Button>
                </div>
                
                {/* Content */}
                <div className="p-6">
                    {/* Product Image Placeholder */}
                    <div className="aspect-video bg-muted rounded-lg mb-6 flex items-center justify-center">
                        <div className="w-24 h-24 bg-gradient-to-br from-muted-foreground/20 to-muted-foreground/10 rounded-lg" />
                    </div>
                    
                    <div className="space-y-4">
                        {product.description && (
                            <div>
                                <h3 className="text-sm font-medium text-muted-foreground mb-1">Description</h3>
                                <p className="text-foreground">{product.description}</p>
                            </div>
                        )}
                        
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <h3 className="text-sm font-medium text-muted-foreground mb-1">Price</h3>
                                <p className="text-2xl font-bold text-primary">
                                    {typeof product.price === 'number' ? product.price.toFixed(2) : product.price} EUR
                                </p>
                            </div>
                            
                            {product.quantityAvailable !== undefined && (
                                <div>
                                    <h3 className="text-sm font-medium text-muted-foreground mb-1">Availability</h3>
                                    <p className={`font-medium ${product.quantityAvailable > 0 ? 'text-emerald-600' : 'text-destructive'}`}>
                                        {product.quantityAvailable > 0 ? `${product.quantityAvailable} in stock` : 'Out of stock'}
                                    </p>
                                </div>
                            )}
                        </div>
                        
                        {product.categoryName && (
                            <div>
                                <h3 className="text-sm font-medium text-muted-foreground mb-1">Category</h3>
                                <span className="inline-flex items-center rounded-full bg-muted px-3 py-1 text-sm font-medium text-muted-foreground">
                                    {product.categoryName}
                                </span>
                            </div>
                        )}
                    </div>
                </div>
                
                {/* Footer */}
                <div className="p-4 border-t border-border bg-muted/30 flex gap-3">
                    {onAddToCart && product.quantityAvailable !== 0 && (
                        <Button onClick={handleAddToCart} className="flex-1">
                            <ShoppingCart className="w-4 h-4 mr-2" />
                            Add to Cart
                        </Button>
                    )}
                    <Button onClick={onClose} variant="outline" className={onAddToCart ? "" : "w-full"}>
                        Close
                    </Button>
                </div>
            </div>
        </div>
    );
};

export default ProductModal;
