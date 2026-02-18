import { keepPreviousData, useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
    fetchProducts,
    fetchCategories,
    fetchBrands,
    fetchCategoryReviews,
    fetchProduct,
    fetchCartItems,
    addToCart,
    deleteCartItem,
    submitCheckout,
    createProduct,
    patchProduct,
    deleteProduct,
    applyDiscount,
    createSubcategory,
    fetchUserProfile,
    updateUserProfile,
    uploadProductImage,
    deleteProductImage,
    setPrimaryProductImage,
    reorderProductImages,
} from '@/api/client';
import {
    ProductFilters,
    CheckoutRequestPayload,
    AdminProduct,
    UserProfile,
    ProductUpsertPayload,
} from '@/types';

export function useProducts(filters: ProductFilters) {
    return useQuery({
        queryKey: ['products', filters],
        queryFn: () => fetchProducts(filters),
        placeholderData: keepPreviousData,
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
        staleTime: 5 * 60 * 1000,
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

export function useCartItems() {
    return useQuery({
        queryKey: ['cartItems'],
        queryFn: fetchCartItems,
    });
}

export function useUserProfile(id: number | undefined) {
    return useQuery({
        queryKey: ['userProfile', id],
        queryFn: () => fetchUserProfile(id!),
        enabled: id !== undefined,
    });
}

export function useAddToCart() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ productId, quantity }: { productId: number; quantity: number }) => addToCart(productId, quantity),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['cartItems'] });
        },
    });
}

export function useDeleteCartItem() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (id: number) => deleteCartItem(id),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['cartItems'] });
        },
    });
}

export function useCheckout() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (payload: CheckoutRequestPayload) => submitCheckout(payload),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['cartItems'] });
        },
    });
}

export function useCreateProduct() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (payload: ProductUpsertPayload) => createProduct(payload),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['products'] });
        },
    });
}

export function usePatchProduct() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ id, data }: { id: number; data: Partial<AdminProduct> }) => patchProduct(id, data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['products'] });
        },
    });
}

export function useDeleteProduct() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (id: number) => deleteProduct(id),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['products'] });
        },
    });
}

export function useApplyDiscount() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ id, discountType }: { id: number; discountType: string }) => applyDiscount(id, discountType),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['products'] });
        },
    });
}

export function useCreateSubcategory() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ parentId, name }: { parentId: number; name: string }) => createSubcategory(parentId, name),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['categories'] });
        },
    });
}

export function useUpdateUserProfile() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ id, data }: { id: number; data: UserProfile }) => updateUserProfile(id, data),
        onSuccess: (_, variables) => {
            queryClient.invalidateQueries({ queryKey: ['userProfile', variables.id] });
        },
    });
}

export function useUploadProductImage() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({
            productId,
            file,
            altText,
            isPrimary,
        }: {
            productId: number;
            file: File;
            altText: string;
            isPrimary: boolean;
        }) => uploadProductImage(productId, file, altText, isPrimary),
        onSuccess: async (_, variables) => {
            await Promise.all([
                queryClient.invalidateQueries({ queryKey: ['products'] }),
                queryClient.invalidateQueries({ queryKey: ['product', variables.productId] }),
            ]);
        },
    });
}

export function useDeleteProductImage() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ productId, imageId }: { productId: number; imageId: number }) =>
            deleteProductImage(productId, imageId),
        onSuccess: async (_, variables) => {
            await Promise.all([
                queryClient.invalidateQueries({ queryKey: ['products'] }),
                queryClient.invalidateQueries({ queryKey: ['product', variables.productId] }),
            ]);
        },
    });
}

export function useSetPrimaryProductImage() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ productId, imageId }: { productId: number; imageId: number }) =>
            setPrimaryProductImage(productId, imageId),
        onSuccess: async (_, variables) => {
            await Promise.all([
                queryClient.invalidateQueries({ queryKey: ['products'] }),
                queryClient.invalidateQueries({ queryKey: ['product', variables.productId] }),
            ]);
        },
    });
}

export function useReorderProductImages() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ productId, imageIds }: { productId: number; imageIds: number[] }) =>
            reorderProductImages(productId, imageIds),
        onSuccess: async (_, variables) => {
            await Promise.all([
                queryClient.invalidateQueries({ queryKey: ['products'] }),
                queryClient.invalidateQueries({ queryKey: ['product', variables.productId] }),
            ]);
        },
    });
}

export function useHydrateProductsWithImages() {
    return useMutation({
        mutationFn: async (productIds: number[]) => {
            const ids = Array.from(new Set(productIds));
            const results = await Promise.all(
                ids.map(async (id) => {
                    try {
                        const product = await fetchProduct(id);
                        return { id, images: product.images };
                    } catch {
                        return { id, images: [] as AdminProduct['images'] };
                    }
                }),
            );
            return results;
        },
    });
}
