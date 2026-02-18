import {
  API_BASE,
  fetchJson,
  fetchVoid,
  type OperationPath,
  type OperationQuery,
  type OperationResponse,
} from '@/api/core';
import { mapCartItem } from '@/api/mappers';
import type { CartItem } from '@/types';

export function fetchCartItems(): Promise<CartItem[]> {
  return fetchJson<OperationResponse<'listMyCartDetails'>>(`${API_BASE}/carts/my/details`).then((items) =>
    items.map(mapCartItem),
  );
}

export function addToCart(productId: number, quantity: number): Promise<void> {
  const pathParams: OperationPath<'addProductToMyCart'> = { productId };
  const query: OperationQuery<'addProductToMyCart'> = { quantity };
  return fetchVoid(`${API_BASE}/carts/my/products/${pathParams.productId}?quantity=${query.quantity}`, {
    method: 'POST',
  });
}

export function deleteCartItem(id: number): Promise<void> {
  const pathParams: OperationPath<'deleteCartDetail'> = { detailId: id };
  return fetchVoid(`${API_BASE}/carts/details/${pathParams.detailId}`, { method: 'DELETE' });
}

export function clearCart(): Promise<void> {
  return fetchVoid(`${API_BASE}/carts/my/clear`, { method: 'DELETE' });
}
