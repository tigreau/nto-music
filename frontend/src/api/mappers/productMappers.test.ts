import { describe, expect, it } from 'vitest';
import { mapDetailedProduct, mapProductImage, mapProductsPage } from '@/api/mappers';

describe('product mappers', () => {
  it('throws when required product fields are missing', () => {
    expect(() =>
      mapDetailedProduct({
        id: 1,
        slug: 'slug-only',
        price: 100,
        condition: 'NEW',
      }),
    ).toThrow('product.name');
  });

  it('throws when product image has no id', () => {
    expect(() => mapProductImage({ url: 'http://image' })).toThrow('productImage.id');
  });

  it('throws when page content item is invalid', () => {
    expect(() =>
      mapProductsPage({
        content: [{ id: 1, name: 'x', slug: 'x', condition: 'NEW' }],
      }),
    ).toThrow('product.price');
  });
});
