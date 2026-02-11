import { useState } from 'react';
import { useCart } from "@/context/CartContext";
import { Hero } from '@/components/Hero';
import { FilterSidebar } from '@/components/FilterSidebar';
import { ProductGrid } from '@/components/ProductGrid';
import { ReviewSection } from '@/components/ReviewSection';
import ProductModal from "@/components/ProductModal";
import { useProducts, useBrands, useCategoryReviews, useCategories } from '@/hooks/useApi';
import { ProductCondition, SortOption, Product } from '@/types';
import { useSearchParams } from 'react-router-dom';
import './HomePage.css';

interface HomePageProps {
    isAdmin: boolean;
}

const HomePage = ({ isAdmin }: HomePageProps) => {
    const [searchParams] = useSearchParams();
    const categorySlug = searchParams.get('category') || undefined;

    // Filter state
    const [selectedBrands, setSelectedBrands] = useState<string[]>([]);
    const [selectedConditions, setSelectedConditions] = useState<ProductCondition[]>([]);
    const [minPrice, setMinPrice] = useState('');
    const [maxPrice, setMaxPrice] = useState('');
    const [sort, setSort] = useState<SortOption>('recommended');
    const [page, setPage] = useState(0);
    const [selectedSubcategory, setSelectedSubcategory] = useState<string | undefined>(undefined);

    // Modal state
    const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
    const [detailedProduct, setDetailedProduct] = useState<any>(null);
    const { refreshCart } = useCart();

    // Queries
    const { data: brands = [] } = useBrands();
    const { data: categories = [] } = useCategories();

    // Determine which category slug to pass to the API
    // If a subcategory is selected, filter by subcategory; otherwise by parent category
    const effectiveCategorySlug = selectedSubcategory || categorySlug;

    const { data: productsPage, isLoading } = useProducts({
        category: effectiveCategorySlug,
        brand: selectedBrands.length > 0 ? selectedBrands.join(',') : undefined,
        minPrice: minPrice ? Number(minPrice) : undefined,
        maxPrice: maxPrice ? Number(maxPrice) : undefined,
        condition: selectedConditions.length > 0 ? selectedConditions.join(',') : undefined,
        sort,
        page,
        size: 20,
    });

    const { data: reviewsData } = useCategoryReviews(categorySlug);

    // Find subcategories for the current parent category
    const activeParent = categories.find(c => c.slug === categorySlug);
    const subCategories = activeParent?.subCategories ?? [];

    // Reset subcategory when parent category changes
    const [prevCategorySlug, setPrevCategorySlug] = useState<string | undefined>(undefined);
    if (categorySlug !== prevCategorySlug) {
        setPrevCategorySlug(categorySlug);
        if (selectedSubcategory) {
            setSelectedSubcategory(undefined);
        }
    }

    const handleProductClick = (product: Product) => {
        setSelectedProduct(product);
        fetch(`/api/products/${product.id}`)
            .then(r => r.json())
            .then(data => setDetailedProduct(data))
            .catch(err => console.error('Error fetching product details:', err));
    };

    const handleCloseModal = () => {
        setSelectedProduct(null);
        setDetailedProduct(null);
    };

    const handleAddToCart = (productId: number, quantity: number) => {
        fetch(`/api/carts/1/products/${productId}?quantity=${quantity}`, { method: 'POST' })
            .then(response => {
                if (response.ok) {
                    alert(`Added ${quantity} item(s) to the cart.`);
                    refreshCart();
                } else {
                    alert('Failed to add items to the cart.');
                }
            })
            .catch(error => console.error('Error adding items to the cart:', error));
    };

    return (
        <div>
            <Hero />
            <div className="shop-layout">
                <FilterSidebar
                    brands={brands}
                    selectedBrands={selectedBrands}
                    onBrandsChange={setSelectedBrands}
                    selectedConditions={selectedConditions}
                    onConditionsChange={setSelectedConditions}
                    minPrice={minPrice}
                    maxPrice={maxPrice}
                    onPriceChange={(min, max) => { setMinPrice(min); setMaxPrice(max); }}
                    sort={sort}
                    onSortChange={setSort}
                    subCategories={subCategories}
                    selectedSubcategory={selectedSubcategory}
                    onSubcategoryChange={setSelectedSubcategory}
                />
                <div className="shop-main">
                    <div className="shop-results-info">
                        {productsPage && (
                            <span>{productsPage.totalElements} product{productsPage.totalElements !== 1 ? 's' : ''}</span>
                        )}
                    </div>
                    <ProductGrid
                        products={productsPage?.content ?? []}
                        onProductClick={handleProductClick}
                        onAddToCart={!isAdmin ? handleAddToCart : undefined}
                        isLoading={isLoading}
                    />
                    {productsPage && productsPage.totalPages > 1 && (
                        <div className="pagination">
                            <button disabled={page === 0} onClick={() => setPage(p => p - 1)}>← Prev</button>
                            <span>Page {page + 1} of {productsPage.totalPages}</span>
                            <button disabled={page >= productsPage.totalPages - 1} onClick={() => setPage(p => p + 1)}>Next →</button>
                        </div>
                    )}

                    {/* Category-specific reviews */}
                    {categorySlug && reviewsData && (
                        <ReviewSection
                            categoryName={reviewsData.categoryName}
                            averageRating={reviewsData.averageRating}
                            reviewCount={reviewsData.reviewCount}
                            reviews={reviewsData.reviews}
                        />
                    )}
                </div>
            </div>

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
