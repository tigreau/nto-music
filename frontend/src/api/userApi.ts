import {
  API_BASE,
  fetchJson,
  putJson,
  type OperationPath,
  type OperationRequestBody,
  type OperationResponse,
} from '@/api/core';
import { mapUserProfile } from '@/api/mappers';
import type { UserProfile } from '@/types';

export function fetchUserProfile(id: number): Promise<UserProfile> {
  const pathParams: OperationPath<'getUser'> = { userId: id };
  return fetchJson<OperationResponse<'getUser'>>(`${API_BASE}/users/${pathParams.userId}`).then(mapUserProfile);
}

export function updateUserProfile(
  id: number,
  data: UserProfile,
): Promise<UserProfile> {
  const pathParams: OperationPath<'updateUser'> = { userId: id };
  const body: OperationRequestBody<'updateUser'> = data;
  return putJson<OperationResponse<'updateUser'>>(`${API_BASE}/users/${pathParams.userId}`, body).then(mapUserProfile);
}
