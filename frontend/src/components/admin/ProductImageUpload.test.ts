import { describe, expect, it } from 'vitest';
import { normalizeImagesAfterDelete } from '@/components/admin/productImageState';
import type { ProductImage } from '@/types';

describe('normalizeImagesAfterDelete', () => {
  it('promotes next image to primary and compacts displayOrder', () => {
    const images: ProductImage[] = [
      { id: 1, url: '/a.png', altText: 'a', isPrimary: true, displayOrder: 0 },
      { id: 2, url: '/b.png', altText: 'b', isPrimary: false, displayOrder: 1 },
      { id: 3, url: '/c.png', altText: 'c', isPrimary: false, displayOrder: 2 },
    ];

    const result = normalizeImagesAfterDelete(images, 1);

    expect(result).toHaveLength(2);
    expect(result[0].id).toBe(2);
    expect(result[0].isPrimary).toBe(true);
    expect(result[0].displayOrder).toBe(0);
    expect(result[1].id).toBe(3);
    expect(result[1].isPrimary).toBe(false);
    expect(result[1].displayOrder).toBe(1);
  });
});
