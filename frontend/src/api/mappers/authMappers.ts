import type { components } from '@/types/generated/openapi';
import type { AuthUser } from '@/types';
import { optionalString, requiredNumber, requiredString } from '@/api/mappers/shared';

type Schemas = components['schemas'];

export function mapAuthResponse(dto: Schemas['AuthResponse']): AuthUser {
  return {
    userId: requiredNumber(dto.userId, 'auth.userId'),
    email: requiredString(dto.email, 'auth.email'),
    firstName: requiredString(dto.firstName, 'auth.firstName'),
    lastName: optionalString(dto.lastName) ?? undefined,
    role: requiredString(dto.role, 'auth.role'),
  };
}
