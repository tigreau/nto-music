import { useState, useEffect } from 'react';
import { useCart } from "@/context/CartContext";
import { Hero } from '@/components/Hero';
import { FeaturedProducts } from '@/components/FeaturedProducts';
import ProductModal from "@/components/ProductModal";
import { useSearchParams } from 'react-router-dom';

interface Product {
    id: number;
    name: string;
    price: number;
    description?: string;
    quantityAvailable?: number;
    categoryName?: string;
}

interface HomePageProps {
    isAdmin: boolean;
}

const HomePage = ({ isAdmin }: HomePageProps) => {
    const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
    const [detailedProduct, setDetailedProduct] = useState<Product | null>(null);
    const { refreshCart } = useCart();
    const [searchParams] = useSearchParams();
    const categoryFilter = searchParams.get('category');

    const handleProductClick = (product: Product) => {
        setSelectedProduct(product);
        // Fetch detailed product info
        fetch(`/api/products/${product.id}`)
            .then(response => response.json())
            .then(data => setDetailedProduct(data))
            .catch(error => console.error('Error fetching product details:', error));
    };

    const handleCloseModal = () => {
        setSelectedProduct(null);
        setDetailedProduct(null);
    };

    const handleAddToCart = (productId: number, quantity: number) => {
        fetch(`/api/carts/1/products/${productId}?quantity=${quantity}`, {
            method: 'POST'
        })
            .then(response => {
                if (response.ok) {
                    alert(`Added ${quantity} item(s) to the cart.`);
                    refreshCart();
                } else {
                    alert('Failed to add items to the cart.');
                }
            })
            .catch(error => {
                console.error('Error adding items to the cart:', error);
            });
    };

    return (
        <div>
            <Hero />
            <FeaturedProducts
                onAddToCart={handleAddToCart}
                onProductClick={handleProductClick}
                isAdmin={isAdmin}
                categoryFilter={categoryFilter}
            />
            {selectedProduct && detailedProduct && (
                <ProductModal
                    product={detailedProduct}
                    onClose={handleCloseModal}
                    onAddToCart={!isAdmin ? handleAddToCart : undefined}
                />
            )}
        </div>
    );
};

export default HomePage;
