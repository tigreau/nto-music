import {
  API_BASE,
  fetchJson,
  fetchVoid,
  patchJson,
  postJson,
  type OperationPath,
  type OperationQuery,
  type OperationRequestBody,
  type OperationResponse,
} from '@/api/core';
import { mapDetailedProduct, mapProductsPage, toProductPatchRequest } from '@/api/mappers';
import type { AdminProduct, DetailedProduct, PageResponse, Product, ProductFilters, ProductUpsertPayload } from '@/types';

export function fetchProducts(filters: ProductFilters = {}): Promise<PageResponse<Product>> {
  const params = new URLSearchParams();
  const query: OperationQuery<'listProducts'> = {
    q: filters.q,
    category: filters.category,
    brand: filters.brand,
    minPrice: filters.minPrice,
    maxPrice: filters.maxPrice,
    condition: filters.condition,
    sort: filters.sort,
    page: filters.page ?? 0,
    size: filters.size ?? 20,
  };

  if (query.q) params.set('q', query.q);
  if (query.category) params.set('category', query.category);
  if (query.brand) params.set('brand', query.brand);
  if (query.minPrice !== undefined) params.set('minPrice', String(query.minPrice));
  if (query.maxPrice !== undefined) params.set('maxPrice', String(query.maxPrice));
  if (query.condition) params.set('condition', query.condition);
  if (query.sort) params.set('sort', query.sort);
  params.set('page', String(query.page ?? 0));
  params.set('size', String(query.size ?? 20));

  return fetchJson<OperationResponse<'listProducts'>>(`${API_BASE}/products?${params.toString()}`).then(mapProductsPage);
}

export function fetchProduct(id: number): Promise<DetailedProduct> {
  const pathParams: OperationPath<'getProductById'> = { id };
  return fetchJson<OperationResponse<'getProductById'>>(`${API_BASE}/products/${pathParams.id}`).then(mapDetailedProduct);
}

export function createProduct(data: ProductUpsertPayload): Promise<DetailedProduct> {
  const body: OperationRequestBody<'createProduct'> = {
    ...data,
    conditionNotes: data.conditionNotes ?? undefined,
  };
  return postJson<OperationResponse<'createProduct'>>(`${API_BASE}/products`, body).then(mapDetailedProduct);
}

export function patchProduct(
  id: number,
  data: Partial<AdminProduct>,
): Promise<DetailedProduct> {
  const body: OperationRequestBody<'partialUpdateProduct'> = toProductPatchRequest(data);
  return patchJson<OperationResponse<'partialUpdateProduct'>>(`${API_BASE}/products/${id}`, body).then(mapDetailedProduct);
}

export function deleteProduct(id: number): Promise<void> {
  return fetchVoid(`${API_BASE}/products/${id}`, { method: 'DELETE' });
}

export function applyDiscount(id: number, discountType: string): Promise<DetailedProduct> {
  const pathParams: OperationPath<'applyDiscount'> = { id };
  const query: OperationQuery<'applyDiscount'> = { discountType };
  const encodedType = encodeURIComponent(query.discountType);
  return patchJson<OperationResponse<'applyDiscount'>>(
    `${API_BASE}/products/${pathParams.id}/apply-discount?discountType=${encodedType}`,
  ).then(mapDetailedProduct);
}
