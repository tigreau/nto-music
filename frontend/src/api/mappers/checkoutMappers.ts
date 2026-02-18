import type { components } from '@/types/generated/openapi';
import type { CheckoutResult } from '@/types';
import { requiredNumber, requiredString } from '@/api/mappers/shared';

type Schemas = components['schemas'];

export function mapCheckoutResult(dto: Schemas['CheckoutResponse']): CheckoutResult {
  return {
    orderId: requiredNumber(dto.orderId, 'checkout.orderId'),
    totalAmount: requiredNumber(dto.totalAmount, 'checkout.totalAmount'),
    paymentStatus: requiredString(dto.paymentStatus, 'checkout.paymentStatus'),
    transactionId: requiredString(dto.transactionId, 'checkout.transactionId'),
  };
}
