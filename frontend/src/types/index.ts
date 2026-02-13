export interface Product {
    id: number;
    name: string;
    slug: string;
    price: number;
    categoryName: string;
    brandName: string | null;
    condition: ProductCondition;
    thumbnailUrl: string | null;
    isPromoted: boolean;
}

export interface DetailedProduct extends Product {
    description: string;
    quantityAvailable: number;
    conditionNotes: string | null;
    images: ProductImage[];
}

export interface ProductImage {
    id: number;
    url: string;
    altText: string;
    isPrimary: boolean;
}

export type ProductCondition = 'NEW' | 'EXCELLENT' | 'VERY_GOOD' | 'GOOD' | 'FAIR';

export const CONDITION_LABELS: Record<ProductCondition, string> = {
    NEW: 'New',
    EXCELLENT: 'Excellent',
    VERY_GOOD: 'Very Good',
    GOOD: 'Good',
    FAIR: 'Fair',
};

export const CONDITION_COLORS: Record<ProductCondition, string> = {
    NEW: '#859900',      // Solarized green
    EXCELLENT: '#2aa198', // Solarized cyan
    VERY_GOOD: '#268bd2', // Solarized blue
    GOOD: '#b58900',     // Solarized yellow
    FAIR: '#cb4b16',     // Solarized orange
};

export interface ProductFilters {
    category?: string;
    brand?: string;
    minPrice?: number;
    maxPrice?: number;
    condition?: string;
    sort?: string;
    page?: number;
    size?: number;
}

export interface PageResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
}

export interface Brand {
    id: number;
    name: string;
    slug: string;
    logoUrl: string | null;
}

export interface Category {
    id: number;
    name: string;
    slug: string;
    description?: string;
    productCount: number;
    subCategories?: Category[];
}

export interface Review {
    id: number;
    userName: string;
    rating: number;
    comment: string;
    productName: string;
    productThumbnailUrl: string | null;
    verifiedPurchase: boolean;
    datePosted: string;
}

export interface CategoryReviews {
    categoryName: string;
    averageRating: number | null;
    reviewCount: number;
    reviews: Review[];
}

export type SortOption = 'recommended' | 'price_asc' | 'price_desc' | 'newest';

export const SORT_OPTIONS: { value: SortOption; label: string }[] = [
    { value: 'recommended', label: 'Recommended' },
    { value: 'price_asc', label: 'Price: Low to High' },
    { value: 'price_desc', label: 'Price: High to Low' },
    { value: 'newest', label: 'Newest' },
];
