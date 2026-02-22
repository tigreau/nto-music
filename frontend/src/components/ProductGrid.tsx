import { Product, CONDITION_LABELS, CONDITION_COLORS } from '@/types';
import { getCategoryImage } from '@/lib/categoryUtils';
import { ShoppingCart } from 'lucide-react';
import './ProductGrid.css';

interface ProductGridProps {
    products: Product[];
    onProductClick: (product: Product) => void;
    onAddToCart?: (productId: number) => void;
    isLoading?: boolean;
}

export function ProductGrid({ products, onProductClick, onAddToCart, isLoading }: ProductGridProps) {
    if (isLoading) {
        return (
            <div className="product-grid">
                {Array.from({ length: 8 }).map((_, i) => (
                    <div key={i} className="product-card skeleton">
                        <div className="skeleton-image" />
                        <div className="skeleton-text" />
                        <div className="skeleton-text short" />
                    </div>
                ))}
            </div>
        );
    }

    if (products.length === 0) {
        return (
            <div className="product-grid-empty">
                <p>No products found matching your filters.</p>
            </div>
        );
    }

    return (
        <div className="product-grid">
            {products.map(product => (
                <div
                    key={product.id}
                    className="product-card"
                    onClick={() => onProductClick(product)}
                >
                    <div className="product-card-image">
                        {(product.thumbnailUrl || getCategoryImage(product.categoryName)) ? (
                            <img
                                src={product.thumbnailUrl || getCategoryImage(product.categoryName)!}
                                alt={product.name}
                            />
                        ) : (
                            <div className="product-card-placeholder">🎵</div>
                        )}
                        {product.isPromoted && (
                            <span className="promoted-badge">★ Featured</span>
                        )}
                    </div>
                    <div className="product-card-body">
                        <span
                            className="condition-badge"
                            style={{ backgroundColor: CONDITION_COLORS[product.condition] + '22', color: CONDITION_COLORS[product.condition], borderColor: CONDITION_COLORS[product.condition] + '44' }}
                        >
                            {CONDITION_LABELS[product.condition]}
                        </span>
                        <h3 className="product-card-name">{product.name}</h3>
                        {product.brandName && (
                            <p className="product-card-brand">{product.brandName}</p>
                        )}
                        <div className="product-card-footer">
                            <span className="product-card-price">${product.price.toFixed(2)}</span>
                            {onAddToCart && (
                                <button
                                    className="add-to-cart-btn"
                                    aria-label={`Add ${product.name} to cart`}
                                    title="Add to cart"
                                    onClick={e => {
                                        e.stopPropagation();
                                        onAddToCart(product.id);
                                    }}
                                >
                                    <ShoppingCart className="add-to-cart-icon" />
                                </button>
                            )}
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
}
