import {
  API_BASE,
  fetchJson,
  postJson,
  type OperationPath,
  type OperationQuery,
  type OperationRequestBody,
  type OperationResponse,
} from '@/api/core';
import { mapCategory, mapCategoryReviews } from '@/api/mappers';
import type { Category, CategoryReviews } from '@/types';

export function fetchCategories(): Promise<Category[]> {
  return fetchJson<OperationResponse<'getAllCategories'>>(`${API_BASE}/categories`).then((items) =>
    items.map(mapCategory),
  );
}

export function fetchCategoryReviews(
  slug: string,
  page = 0,
  size = 10,
): Promise<CategoryReviews> {
  const pathParams: OperationPath<'getCategoryReviews'> = { slug };
  const query: OperationQuery<'getCategoryReviews'> = { page, size };
  return fetchJson<OperationResponse<'getCategoryReviews'>>(
    `${API_BASE}/categories/${pathParams.slug}/reviews?page=${query.page ?? 0}&size=${query.size ?? 10}`,
  ).then(mapCategoryReviews);
}

export function createSubcategory(parentId: number, categoryName: string): Promise<Category> {
  const query: OperationQuery<'createCategory'> = { parentId };
  const body: OperationRequestBody<'createCategory'> = { categoryName };
  return postJson<OperationResponse<'createCategory'>>(`${API_BASE}/categories?parentId=${query.parentId}`, body).then(
    mapCategory,
  );
}
