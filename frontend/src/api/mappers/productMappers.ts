import type { components } from '@/types/generated/openapi';
import type { AdminProduct, DetailedProduct, PageResponse, Product, ProductImage } from '@/types';
import { asCondition, optionalString, requiredNumber, requiredString } from '@/api/mappers/shared';

type Schemas = components['schemas'];

export function mapProductImage(dto: Schemas['ProductImageDTO']): ProductImage {
  return {
    id: requiredNumber(dto.id, 'productImage.id'),
    url: requiredString(dto.url, 'productImage.url'),
    altText: optionalString(dto.altText) ?? '',
    isPrimary: !!dto.primary,
    displayOrder: dto.displayOrder ?? 0,
  };
}

export function mapSimpleProduct(dto: Schemas['SimpleProductDTO']): Product {
  return {
    id: requiredNumber(dto.id, 'product.id'),
    name: requiredString(dto.name, 'product.name'),
    slug: requiredString(dto.slug, 'product.slug'),
    price: requiredNumber(dto.price, 'product.price'),
    categoryName: optionalString(dto.categoryName) ?? 'Unknown',
    brandName: optionalString(dto.brandName),
    condition: asCondition(dto.condition, 'product.condition'),
    thumbnailUrl: optionalString(dto.thumbnailUrl),
    isPromoted: !!dto.isPromoted,
    images: Array.isArray(dto.images) ? dto.images.map(mapProductImage) : undefined,
  };
}

export function mapDetailedProduct(dto: Schemas['DetailedProductDTO']): DetailedProduct {
  return {
    ...mapSimpleProduct(dto),
    description: optionalString(dto.description) ?? '',
    quantityAvailable: dto.quantityAvailable ?? 0,
    conditionNotes: optionalString(dto.conditionNotes),
    images: Array.isArray(dto.images) ? dto.images.map(mapProductImage) : [],
  };
}

export function mapProductsPage(dto: Schemas['PageSimpleProductDTO']): PageResponse<Product> {
  return {
    content: Array.isArray(dto.content) ? dto.content.map(mapSimpleProduct) : [],
    totalElements: dto.totalElements ?? 0,
    totalPages: dto.totalPages ?? 0,
    number: dto.number ?? 0,
    size: dto.size ?? 0,
  };
}

export function mapAdminProduct(dto: Schemas['DetailedProductDTO']): AdminProduct {
  return {
    id: requiredNumber(dto.id, 'adminProduct.id'),
    name: requiredString(dto.name, 'adminProduct.name'),
    slug: requiredString(dto.slug, 'adminProduct.slug'),
    price: requiredNumber(dto.price, 'adminProduct.price'),
    description: optionalString(dto.description) ?? '',
    condition: asCondition(dto.condition, 'adminProduct.condition'),
    quantityAvailable: dto.quantityAvailable ?? 0,
    category: null,
    images: Array.isArray(dto.images) ? dto.images.map(mapProductImage) : [],
  };
}

export function toProductPatchRequest(data: Partial<AdminProduct>): Schemas['ProductPatchRequest'] {
  return {
    name: data.name,
    description: data.description,
    price: data.price,
    quantityAvailable: data.quantityAvailable,
    condition: data.condition,
  };
}
