import { describe, expect, it } from 'vitest';
import { ApiError, getApiErrorPolicy, toApiError, toUnknownApiError } from '@/lib/apiError';

describe('apiError', () => {
  it('maps backend payload to typed ApiError', () => {
    const response = new Response(JSON.stringify({}), { status: 403, statusText: 'Forbidden' });
    const error = toApiError(response, {
      code: 'ACCESS_DENIED',
      message: 'No permission',
      status: 403,
      timestamp: '2026-02-17T00:00:00Z',
      error: 'Forbidden',
    });

    expect(error).toBeInstanceOf(ApiError);
    expect(error.status).toBe(403);
    expect(error.code).toBe('ACCESS_DENIED');
    expect(error.message).toBe('No permission');
    expect(error.reason).toBe('Forbidden');
  });

  it('returns policy redirect for unauthorized code', () => {
    const policy = getApiErrorPolicy(
      new ApiError({
        message: 'Session expired',
        status: 401,
        code: 'UNAUTHORIZED',
      }),
    );

    expect(policy.action).toBe('redirect_login');
    expect(policy.message).toContain('session');
  });

  it('normalizes unknown thrown values', () => {
    const error = toUnknownApiError('bad');
    expect(error.code).toBe('UNKNOWN_ERROR');
    expect(error.status).toBe(0);
  });
});
