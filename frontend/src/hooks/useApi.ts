import { useQuery } from '@tanstack/react-query';
import { fetchProducts, fetchCategories, fetchBrands, fetchCategoryReviews, fetchProduct } from '@/api/client';
import { ProductFilters } from '@/types';

export function useProducts(filters: ProductFilters) {
    return useQuery({
        queryKey: ['products', filters],
        queryFn: () => fetchProducts(filters),
        keepPreviousData: true,
    });
}

export function useProduct(id: number | null) {
    return useQuery({
        queryKey: ['product', id],
        queryFn: () => fetchProduct(id!),
        enabled: id !== null,
    });
}

export function useCategories() {
    return useQuery({
        queryKey: ['categories'],
        queryFn: fetchCategories,
        staleTime: 5 * 60 * 1000, // categories rarely change
    });
}

export function useBrands() {
    return useQuery({
        queryKey: ['brands'],
        queryFn: fetchBrands,
        staleTime: 5 * 60 * 1000,
    });
}

export function useCategoryReviews(slug: string | undefined, page = 0, size = 10) {
    return useQuery({
        queryKey: ['categoryReviews', slug, page, size],
        queryFn: () => fetchCategoryReviews(slug!, page, size),
        enabled: !!slug,
    });
}
