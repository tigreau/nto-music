import type { components } from '@/types/generated/openapi';
import type { CartItem, CartProduct } from '@/types';
import { optionalString, requiredNumber, requiredString } from '@/api/mappers/shared';

type Schemas = components['schemas'];

function mapCartProduct(dto: Schemas['CartProductDTO']): CartProduct {
  return {
    id: requiredNumber(dto.id, 'cart.product.id'),
    name: requiredString(dto.name, 'cart.product.name'),
    slug: requiredString(dto.slug, 'cart.product.slug'),
    price: requiredNumber(dto.price, 'cart.product.price'),
    thumbnailUrl: optionalString(dto.thumbnailUrl),
    category: dto.category
      ? {
          id: requiredNumber(dto.category.id, 'cart.product.category.id'),
          name: requiredString(dto.category.name, 'cart.product.category.name'),
          slug: requiredString(dto.category.slug, 'cart.product.category.slug'),
        }
      : null,
    brand: dto.brand
      ? {
          id: requiredNumber(dto.brand.id, 'cart.product.brand.id'),
          name: requiredString(dto.brand.name, 'cart.product.brand.name'),
          slug: requiredString(dto.brand.slug, 'cart.product.brand.slug'),
        }
      : null,
  };
}

export function mapCartItem(dto: Schemas['CartItemDTO']): CartItem {
  const product = dto.product;
  if (!product) {
    throw new Error('Invalid API response: cart item product is required');
  }
  return {
    id: requiredNumber(dto.id, 'cart.id'),
    quantity: dto.quantity ?? 0,
    product: mapCartProduct(product),
    subTotal: dto.subTotal ?? 0,
  };
}
