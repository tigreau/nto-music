import type { components } from '@/types/generated/openapi';
import type { Brand, Category, CategoryReviews } from '@/types';
import { optionalString, requiredNumber, requiredString } from '@/api/mappers/shared';

type Schemas = components['schemas'];

export function mapBrand(dto: Schemas['BrandDTO']): Brand {
  return {
    id: requiredNumber(dto.id, 'brand.id'),
    name: requiredString(dto.name, 'brand.name'),
    slug: requiredString(dto.slug, 'brand.slug'),
    logoUrl: optionalString(dto.logoUrl),
  };
}

export function mapCategory(dto: Schemas['CategoryDTO']): Category {
  return {
    id: requiredNumber(dto.id, 'category.id'),
    name: requiredString(dto.name, 'category.name'),
    slug: requiredString(dto.slug, 'category.slug'),
    description: optionalString(dto.description) ?? undefined,
    productCount: dto.productCount ?? 0,
    subCategories: Array.isArray(dto.subCategories) ? dto.subCategories.map(mapCategory) : [],
  };
}

export function mapCategoryReviews(dto: Schemas['CategoryReviewsDTO']): CategoryReviews {
  return {
    categoryName: optionalString(dto.categoryName) ?? 'Unknown category',
    averageRating: typeof dto.averageRating === 'number' ? dto.averageRating : null,
    reviewCount: dto.reviewCount ?? 0,
    reviews: Array.isArray(dto.reviews)
      ? dto.reviews.map((review) => ({
          id: requiredNumber(review.id, 'review.id'),
          userName: requiredString(review.userName, 'review.userName'),
          rating: requiredNumber(review.rating, 'review.rating'),
          comment: requiredString(review.comment, 'review.comment'),
          productName: requiredString(review.productName, 'review.productName'),
          productThumbnailUrl: optionalString(review.productThumbnailUrl),
          verifiedPurchase: !!review.verifiedPurchase,
          datePosted: requiredString(review.datePosted, 'review.datePosted'),
        }))
      : [],
  };
}
