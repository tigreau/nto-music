import { API_BASE, fetchJson, type OperationResponse } from '@/api/core';
import { mapBrand } from '@/api/mappers';
import type { Brand } from '@/types';

export function fetchBrands(): Promise<Brand[]> {
  return fetchJson<OperationResponse<'getAllBrands'>>(`${API_BASE}/brands`).then((items) => items.map(mapBrand));
}
