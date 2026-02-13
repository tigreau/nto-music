import { Button } from "@/components/ui/button";
import { ShoppingCart, ChevronLeft, ChevronRight } from "lucide-react";
import { getCategoryImage } from '@/lib/categoryUtils';
import { useState, useEffect } from "react";
import { Badge } from "@/components/ui/badge";

interface ProductImage {
    id: number;
    url: string;
    altText: string;
    isPrimary: boolean;
    displayOrder: number;
}

interface Product {
    id: number;
    name: string;
    price: number;
    description?: string;
    quantityAvailable?: number;
    categoryName?: string;
    condition?: string;
    images?: ProductImage[];
}

interface ProductModalProps {
    product: Product;
    onClose: () => void;
    onAddToCart?: (productId: number, quantity: number) => void;
}

const ProductModal = ({ product, onClose, onAddToCart }: ProductModalProps) => {
    const [currentImageIndex, setCurrentImageIndex] = useState(0);

    // Sort images by displayOrder
    const sortedImages = product.images?.slice().sort((a, b) => a.displayOrder - b.displayOrder) || [];

    useEffect(() => {
        setCurrentImageIndex(0);
    }, [product]);

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

    const nextImage = () => {
        setCurrentImageIndex((prev) => (prev + 1) % sortedImages.length);
    };

    const prevImage = () => {
        setCurrentImageIndex((prev) => (prev - 1 + sortedImages.length) % sortedImages.length);
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-0 sm:p-4">
            {/* Backdrop */}
            <div
                className="absolute inset-0 bg-background/90 backdrop-blur-sm transition-opacity"
                onClick={onClose}
            />

            {/* Modal */}
            <div className="relative bg-transparent w-full h-full flex flex-col">

                {/* Content - Scrollable */}
                <div className="overflow-y-auto scrollbar-thin p-2 lg:p-8 h-full">
                    <div className="flex flex-col lg:flex-row gap-8 items-start lg:h-full">
                        {/* Left Column: Image + Description */}
                        <div className="w-full lg:w-2/3 flex flex-col gap-8 lg:h-full">
                            {/* Image Carousel */}
                            <div className="relative w-full aspect-video lg:aspect-auto lg:h-full rounded-lg overflow-hidden flex items-center justify-center group shrink-0">
                                {sortedImages.length > 0 ? (
                                    <>
                                        <img
                                            src={sortedImages[currentImageIndex].url}
                                            alt={sortedImages[currentImageIndex].altText || product.name}
                                            className="w-full h-full object-contain mix-blend-multiply dark:mix-blend-normal"
                                        />

                                        {/* Navigation Arrows */}
                                        {sortedImages.length > 1 && (
                                            <>
                                                <Button
                                                    variant="secondary"
                                                    size="icon"
                                                    className="absolute left-2 top-1/2 -translate-y-1/2 opacity-0 group-hover:opacity-100 transition-opacity rounded-full shadow-md bg-foreground/50 hover:bg-foreground/70 text-background"
                                                    onClick={(e) => { e.stopPropagation(); prevImage(); }}
                                                >
                                                    <ChevronLeft className="w-5 h-5" />
                                                </Button>
                                                <Button
                                                    variant="secondary"
                                                    size="icon"
                                                    className="absolute right-2 top-1/2 -translate-y-1/2 opacity-0 group-hover:opacity-100 transition-opacity rounded-full shadow-md bg-foreground/50 hover:bg-foreground/70 text-background"
                                                    onClick={(e) => { e.stopPropagation(); nextImage(); }}
                                                >
                                                    <ChevronRight className="w-5 h-5" />
                                                </Button>

                                                {/* Indicators */}
                                                <div className="absolute bottom-2 left-1/2 -translate-x-1/2 flex gap-1.5 p-1.5 bg-background/60 backdrop-blur rounded-full">
                                                    {sortedImages.map((_, idx) => (
                                                        <div
                                                            key={idx}
                                                            className={`w-2 h-2 rounded-full transition-colors ${idx === currentImageIndex ? 'bg-primary' : 'bg-muted-foreground/30'
                                                                }`}
                                                        />
                                                    ))}
                                                </div>
                                            </>
                                        )}
                                    </>
                                ) : (
                                    /* Fallback Image */
                                    getCategoryImage(product.categoryName) ? (
                                        <img
                                            src={getCategoryImage(product.categoryName)!}
                                            alt={product.name}
                                            className="w-full h-full object-contain mix-blend-multiply dark:mix-blend-normal"
                                        />
                                    ) : (
                                        <div className="w-24 h-24 bg-gradient-to-br from-muted-foreground/20 to-muted-foreground/10 rounded-lg flex items-center justify-center">
                                            <span className="text-6xl">🎵</span>
                                        </div>
                                    )
                                )}
                            </div>

                        </div>

                        {/* Right Column: Product Details Card */}
                        <div className="w-full lg:w-1/3 bg-card rounded-xl border border-border shadow-xl overflow-hidden flex flex-col sticky top-8 lg:h-full">
                            <div className="p-6 space-y-6 flex-1 overflow-y-auto scrollbar-thin">
                                {/* Product Name */}
                                <div>
                                    <h2 className="text-3xl font-bold text-foreground leading-tight">{product.name}</h2>
                                </div>

                                {/* Product Info */}
                                <div className="space-y-6">
                                    <div className="space-y-4">
                                        <div className="flex items-center gap-2 flex-wrap">
                                            {product.categoryName && (
                                                <Badge variant="outline" className="text-muted-foreground text-sm py-1 px-3">
                                                    {product.categoryName}
                                                </Badge>
                                            )}
                                            {product.condition && (
                                                <Badge variant="secondary" className="uppercase text-xs font-bold tracking-wide py-1 px-3">
                                                    {product.condition}
                                                </Badge>
                                            )}
                                        </div>

                                        <div className="pt-2">
                                            <div className="text-4xl font-bold text-primary">
                                                {typeof product.price === 'number' ? product.price.toFixed(2) : product.price} EUR
                                            </div>
                                        </div>
                                        {product.quantityAvailable !== undefined && (
                                            <p className={`text-sm font-medium mt-2 ${product.quantityAvailable > 0 ? 'text-[#859900]' : 'text-destructive'}`}>
                                                {product.quantityAvailable > 0 ? `In Stock (${product.quantityAvailable} available)` : 'Out of Stock'}
                                            </p>
                                        )}
                                    </div>

                                    <div className="pt-4 border-t border-border">
                                        <h3 className="text-lg font-semibold text-foreground mb-2">Description</h3>
                                        <p className="text-foreground/90 leading-relaxed">
                                            {product.description || "No description provided."}
                                        </p>
                                    </div>
                                </div>
                            </div>

                            {/* Footer */}
                            <div className="p-6 border-t border-border bg-muted/30 flex flex-col gap-3">
                                {onAddToCart && product.quantityAvailable !== 0 && (
                                    <Button
                                        onClick={handleAddToCart}
                                        className="w-full text-lg py-6 shadow-md hover:shadow-lg transition-all font-bold"
                                        size="lg"
                                    >
                                        <ShoppingCart className="w-6 h-6 mr-2" />
                                        Add to Cart
                                    </Button>
                                )}
                                <Button
                                    onClick={onClose}
                                    variant="outline"
                                    size="lg"
                                    className={`w-full text-base py-6`}
                                >
                                    Close
                                </Button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ProductModal;
