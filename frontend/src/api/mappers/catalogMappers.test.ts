import { describe, expect, it } from 'vitest';
import { mapBrand, mapCategory, mapCategoryReviews } from '@/api/mappers';

describe('catalog mappers', () => {
  it('throws when brand is missing required slug', () => {
    expect(() => mapBrand({ id: 1, name: 'Brand' })).toThrow('brand.slug');
  });

  it('throws when category is missing required name', () => {
    expect(() => mapCategory({ id: 1, slug: 'instruments' })).toThrow('category.name');
  });

  it('throws when review has missing required fields', () => {
    expect(() =>
      mapCategoryReviews({
        categoryName: 'Guitars',
        reviews: [{ id: 1, rating: 4, comment: 'ok' }],
      }),
    ).toThrow('review.userName');
  });
});
