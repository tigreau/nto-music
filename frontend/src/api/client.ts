import { ProductFilters, PageResponse, Product, DetailedProduct, Brand, Category, CategoryReviews } from '@/types';

const API_BASE = '/api';

async function fetchJson<T>(url: string): Promise<T> {
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error(`API error: ${response.status} ${response.statusText}`);
    }
    return response.json();
}

// Products
export function fetchProducts(filters: ProductFilters = {}): Promise<PageResponse<Product>> {
    const params = new URLSearchParams();
    if (filters.category) params.set('category', filters.category);
    if (filters.brand) params.set('brand', filters.brand);
    if (filters.minPrice !== undefined) params.set('minPrice', String(filters.minPrice));
    if (filters.maxPrice !== undefined) params.set('maxPrice', String(filters.maxPrice));
    if (filters.condition) params.set('condition', filters.condition);
    if (filters.sort) params.set('sort', filters.sort);
    params.set('page', String(filters.page ?? 0));
    params.set('size', String(filters.size ?? 20));

    return fetchJson(`${API_BASE}/products?${params.toString()}`);
}

export function fetchProduct(id: number): Promise<DetailedProduct> {
    return fetchJson(`${API_BASE}/products/${id}`);
}

// Categories
export function fetchCategories(): Promise<Category[]> {
    return fetchJson(`${API_BASE}/categories`);
}

export function fetchCategoryReviews(slug: string, page = 0, size = 10): Promise<CategoryReviews> {
    return fetchJson(`${API_BASE}/categories/${slug}/reviews?page=${page}&size=${size}`);
}

// Brands
export function fetchBrands(): Promise<Brand[]> {
    return fetchJson(`${API_BASE}/brands`);
}
