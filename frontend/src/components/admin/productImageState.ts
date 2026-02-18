import type { ProductImage } from '@/types';

export function normalizeImagesAfterDelete(images: ProductImage[], deletedImageId: number): ProductImage[] {
  return images
    .filter((img) => img.id !== deletedImageId)
    .sort((a, b) => a.displayOrder - b.displayOrder)
    .map((img, index) => ({
      ...img,
      displayOrder: index,
      isPrimary: index === 0,
    }));
}
