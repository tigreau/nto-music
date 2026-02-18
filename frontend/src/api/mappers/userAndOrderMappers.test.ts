import { describe, expect, it } from 'vitest';
import { mapAuthResponse, mapCheckoutResult, mapUserProfile } from '@/api/mappers';

describe('user/order mappers', () => {
  it('throws when auth payload misses user id', () => {
    expect(() =>
      mapAuthResponse({
        email: 'u@test.com',
        firstName: 'U',
        role: 'USER',
      }),
    ).toThrow('auth.userId');
  });

  it('throws when checkout response misses transaction id', () => {
    expect(() => mapCheckoutResult({ orderId: 1, totalAmount: 99, paymentStatus: 'PAID' })).toThrow(
      'checkout.transactionId',
    );
  });

  it('throws when user profile misses firstName', () => {
    expect(() => mapUserProfile({ lastName: 'L', email: 'u@test.com' })).toThrow('user.firstName');
  });
});
