import type { components } from '@/types/generated/openapi';
import type { UserProfile } from '@/types';
import { optionalString, requiredString } from '@/api/mappers/shared';

type Schemas = components['schemas'];

export function mapUserProfile(dto: Schemas['UserDTO']): UserProfile {
  return {
    firstName: requiredString(dto.firstName, 'user.firstName'),
    lastName: requiredString(dto.lastName, 'user.lastName'),
    email: requiredString(dto.email, 'user.email'),
    phoneNumber: optionalString(dto.phoneNumber) ?? '',
  };
}
