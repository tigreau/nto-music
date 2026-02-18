import { Category, Product } from '@/types';

export interface NavCategory {
    name: string;
    slug: string;
    href: string;
}

export interface NavCategoryWithCount extends NavCategory {
    count: number;
}

export function mapCategoryCounts(navCategories: NavCategory[], categories: Category[]): NavCategoryWithCount[] {
    return navCategories.map((fixed) => {
        const match = categories.find((category) => category.slug === fixed.slug);
        return { ...fixed, count: match ? match.productCount : 0 };
    });
}

export function filterProductsByCategory<T extends Product>(products: T[], categoryFilter?: string | null): T[] {
    if (!categoryFilter) return products;
    const normalizedFilter = categoryFilter.toLowerCase();
    return products.filter((product) => (product.categoryName || '').toLowerCase() === normalizedFilter);
}

export function sortProductsByOption<T extends Product>(products: T[], sortBy: string): T[] {
    return [...products].sort((a, b) => {
        switch (sortBy) {
            case 'Price: Low to High':
                return a.price - b.price;
            case 'Price: High to Low':
                return b.price - a.price;
            case 'Newest':
                return b.id - a.id;
            default:
                return 0;
        }
    });
}
