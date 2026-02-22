import { describe, expect, it } from 'vitest';
import {
  filterProductsByCategory,
  mapCategoryCounts,
  sortProductsByOption,
  type NavCategory,
} from '@/lib/productView';
import type { Category, Product } from '@/types';

const products: Product[] = [
  {
    id: 2,
    name: 'B Product',
    slug: 'b-product',
    price: 50,
    categoryName: 'Guitars',
    brandName: null,
    condition: 'GOOD',
    thumbnailUrl: null,
    isPromoted: false,
  },
  {
    id: 3,
    name: 'C Product',
    slug: 'c-product',
    price: 20,
    categoryName: 'Drums',
    brandName: null,
    condition: 'EXCELLENT',
    thumbnailUrl: null,
    isPromoted: false,
  },
];

describe('productView utilities', () => {
  it('maps category counts by slug', () => {
    const nav: NavCategory[] = [
      { name: 'Guitars', slug: 'guitars', href: '/?category=Guitars' },
      { name: 'Drums', slug: 'drums', href: '/?category=Drums' },
    ];
    const categories: Category[] = [
      { id: 1, name: 'Guitars', slug: 'guitars', productCount: 11 },
      { id: 2, name: 'Drums', slug: 'drums', productCount: 4 },
    ];

    const result = mapCategoryCounts(nav, categories);
    expect(result[0].count).toBe(11);
    expect(result[1].count).toBe(4);
  });

  it('filters by case-insensitive category', () => {
    const result = filterProductsByCategory(products, 'guitars');
    expect(result).toHaveLength(1);
    expect(result[0].slug).toBe('b-product');
  });

  it('sorts by price asc', () => {
    const result = sortProductsByOption(products, 'Price: Low to High');
    expect(result.map((item) => item.slug)).toEqual(['c-product', 'b-product']);
  });
});
