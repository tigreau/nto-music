import {
  API_BASE,
  postJson,
  type OperationRequestBody,
  type OperationResponse,
} from '@/api/core';
import { mapCheckoutResult } from '@/api/mappers';
import type { CheckoutRequestPayload, CheckoutResult } from '@/types';

export function submitCheckout(data: CheckoutRequestPayload): Promise<CheckoutResult> {
  const body: OperationRequestBody<'checkout'> = data;
  return postJson<OperationResponse<'checkout'>>(`${API_BASE}/orders/checkout`, body).then(mapCheckoutResult);
}
