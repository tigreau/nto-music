export type ProductCondition = 'EXCELLENT' | 'GOOD' | 'FAIR';

export const CONDITION_LABELS: Record<ProductCondition, string> = {
  EXCELLENT: 'Excellent',
  GOOD: 'Good',
  FAIR: 'Fair',
};

export const CONDITION_COLORS: Record<ProductCondition, string> = {
  EXCELLENT: '#2aa198',
  GOOD: '#b58900',
  FAIR: '#cb4b16',
};

export interface ProductImage {
  id: number;
  url: string;
  altText: string;
  isPrimary: boolean;
  displayOrder: number;
}

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
  images?: ProductImage[];
}

export interface DetailedProduct extends Product {
  description: string;
  quantityAvailable: number;
  conditionNotes: string | null;
  images: ProductImage[];
}

export interface AdminProduct {
  id: number;
  name: string;
  slug: string;
  price: number;
  description: string;
  condition: ProductCondition;
  quantityAvailable: number;
  category: { id: number; name?: string } | null;
  images: ProductImage[];
}

export interface ProductUpsertPayload {
  name: string;
  description: string;
  price: number;
  quantityAvailable: number;
  categoryId: number;
  condition: ProductCondition;
  conditionNotes?: string | null;
}

export interface ProductFilters {
  q?: string;
  category?: string;
  brand?: string;
  minPrice?: number;
  maxPrice?: number;
  condition?: string;
  sort?: string;
  page?: number;
  size?: number;
}

export type SortOption = 'recommended' | 'price_asc' | 'price_desc' | 'newest';

export const SORT_OPTIONS: { value: SortOption; label: string }[] = [
  { value: 'recommended', label: 'Recommended' },
  { value: 'price_asc', label: 'Price: Low to High' },
  { value: 'price_desc', label: 'Price: High to Low' },
  { value: 'newest', label: 'Newest' },
];
