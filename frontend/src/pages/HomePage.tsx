import { useEffect, useRef, useState } from 'react';
import { useCart } from "@/context/CartContext";
import { Hero } from '@/components/Hero';
import { FilterSidebar } from '@/components/FilterSidebar';
import { ProductGrid } from '@/components/ProductGrid';
import { ReviewSection } from '@/components/ReviewSection';
import ProductModal from "@/components/ProductModal";
import { AsyncPageState } from '@/components/state/AsyncPageState';
import { useProducts, useBrands, useCategoryReviews, useCategories, useProduct, useAddToCart } from '@/hooks/useApi';
import { getApiErrorPolicy } from '@/lib/apiError';
import { ProductCondition, SortOption, Product } from '@/types';
import { useSearchParams } from 'react-router-dom';
import { useMutationFeedback } from '@/hooks/useMutationFeedback';
import './HomePage.css';

interface HomePageProps {
    isAdmin: boolean;
}

const HomePage = ({ isAdmin }: HomePageProps) => {
    const [searchParams] = useSearchParams();
    const categorySlug = searchParams.get('category') || undefined;
    const searchQuery = searchParams.get('q') || undefined;

    // Filter state
    const [selectedBrands, setSelectedBrands] = useState<string[]>([]);
    const [selectedConditions, setSelectedConditions] = useState<ProductCondition[]>([]);
    const [minPrice, setMinPrice] = useState('');
    const [maxPrice, setMaxPrice] = useState('');
    const [sort, setSort] = useState<SortOption>('recommended');
    const [page, setPage] = useState(0);
    const [selectedSubcategory, setSelectedSubcategory] = useState<string | undefined>(undefined);

    // Modal state
    const [selectedProductId, setSelectedProductId] = useState<number | null>(null);
    const productsSectionRef = useRef<HTMLDivElement | null>(null);
    const previousSearchQueryRef = useRef<string | undefined>(searchQuery);
    const { refreshCart } = useCart();
    const addToCartMutation = useAddToCart();
    const runWithFeedback = useMutationFeedback();

    // Queries
    const { data: brands = [] } = useBrands();
    const { data: categories = [] } = useCategories();

    // Determine which category slug to pass to the API
    // If a subcategory is selected, filter by subcategory; otherwise by parent category
    const effectiveCategorySlug = selectedSubcategory || categorySlug;

    const {
        data: productsPage,
        isLoading,
        isError,
        error,
        refetch,
    } = useProducts({
        q: searchQuery,
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
    const { data: detailedProduct } = useProduct(selectedProductId);

    // Find subcategories for the current parent category
    const activeParent = categories.find(c => c.slug === categorySlug);
    const subCategories = activeParent?.subCategories ?? [];

    // Reset selected subcategory when parent category from URL changes
    // to avoid applying stale nested filters.
    useEffect(() => {
        setSelectedSubcategory(undefined);
    }, [categorySlug]);

    useEffect(() => {
        setPage(0);
    }, [searchQuery, categorySlug, selectedSubcategory, selectedBrands, selectedConditions, minPrice, maxPrice, sort]);

    useEffect(() => {
        const previousSearch = previousSearchQueryRef.current;
        previousSearchQueryRef.current = searchQuery;

        const hasNewSearch = Boolean(searchQuery) && searchQuery !== previousSearch;
        if (!hasNewSearch || !productsSectionRef.current) {
            return;
        }

        const headerOffset = 84;
        const top =
            productsSectionRef.current.getBoundingClientRect().top + window.scrollY - headerOffset;

        window.requestAnimationFrame(() => {
            window.scrollTo({ top: Math.max(0, top), behavior: 'smooth' });
        });
    }, [searchQuery]);

    const handleProductClick = (product: Product) => {
        setSelectedProductId(product.id);
    };

    const handleCloseModal = () => {
        setSelectedProductId(null);
    };

    const handleAddToCart = (productId: number) => {
        void runWithFeedback(
            () => addToCartMutation.mutateAsync({ productId, quantity: 1 }),
            {
                context: 'home.addToCart',
                successMessage: 'Added to cart',
                onSuccess: () => {
                    void refreshCart();
                },
            },
        );
    };

    return (
        <div>
            <Hero />
            <AsyncPageState
                isError={isError}
                errorMessage={getApiErrorPolicy(error).message}
                onRetry={() => { refetch(); }}
                loadingClassName="py-16"
            >
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
                    <div className="shop-main" ref={productsSectionRef}>
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
            </AsyncPageState>

            {selectedProductId && detailedProduct && (
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
