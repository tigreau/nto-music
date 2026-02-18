import {
  API_BASE,
  fetchJson,
  fetchVoid,
  putJson,
  type OperationPath,
  type OperationRequestBody,
  type OperationResponse,
} from '@/api/core';
import { mapProductImage } from '@/api/mappers';
import type { ProductImage } from '@/types';

export function uploadProductImage(
  productId: number,
  file: File,
  altText: string,
  isPrimary: boolean,
): Promise<ProductImage> {
  const pathParams: OperationPath<'uploadImage'> = { productId };
  const formData = new FormData();
  formData.append('file', file);
  formData.append('isPrimary', String(isPrimary));
  formData.append('altText', altText);

  return fetchJson<OperationResponse<'uploadImage'>>(`${API_BASE}/products/${pathParams.productId}/images`, {
    method: 'POST',
    body: formData,
  }).then(mapProductImage);
}

export function deleteProductImage(productId: number, imageId: number): Promise<void> {
  return fetchVoid(`${API_BASE}/products/${productId}/images/${imageId}`, {
    method: 'DELETE',
  });
}

export function setPrimaryProductImage(productId: number, imageId: number): Promise<void> {
  const pathParams: OperationPath<'setPrimaryImage'> = { productId, imageId };
  return fetchVoid(`${API_BASE}/products/${pathParams.productId}/images/${pathParams.imageId}/primary`, {
    method: 'PATCH',
  });
}

export function reorderProductImages(productId: number, imageIds: number[]): Promise<void> {
  const pathParams: OperationPath<'reorderImages'> = { productId };
  const body: OperationRequestBody<'reorderImages'> = { imageIds };
  return putJson<void>(`${API_BASE}/products/${pathParams.productId}/images/reorder`, body);
}
