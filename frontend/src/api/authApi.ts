import {
  API_BASE,
  fetchJson,
  fetchVoid,
  postJson,
  type OperationRequestBody,
  type OperationResponse,
} from '@/api/core';
import type { AuthUser } from '@/types';
import { mapAuthResponse } from '@/api/mappers';
import {
  readStoredUser,
  writeStoredUser,
  clearStoredUser as clearStoredUserFromStorage,
} from '@/lib/authStorage';
import { toUnknownApiError } from '@/lib/apiError';

type AuthResponseSchema = OperationResponse<'login'>;
export interface AuthResponse extends AuthUser {
  token: string | null;
}

function toAuthResponse(response: AuthResponseSchema): AuthResponse {
  const user = mapAuthResponse(response);
  return { ...user, token: response.token ?? null };
}

export function getStoredUser(): AuthUser | null {
  return readStoredUser();
}

export function setStoredUser(user: AuthUser) {
  writeStoredUser(user);
}

export function clearStoredUser() {
  clearStoredUserFromStorage();
}

export async function login(email: string, password: string): Promise<AuthResponse> {
  const body: OperationRequestBody<'login'> = { email, password };
  const response = await postJson<AuthResponseSchema>(`${API_BASE}/auth/login`, body);
  const mapped = toAuthResponse(response);
  setStoredUser(mapped);
  return mapped;
}

export async function register(
  firstName: string,
  lastName: string,
  email: string,
  password: string,
): Promise<AuthResponse> {
  const body: OperationRequestBody<'register'> = { firstName, lastName, email, password };
  const response = await postJson<AuthResponseSchema>(`${API_BASE}/auth/register`, body);
  const mapped = toAuthResponse(response);
  setStoredUser(mapped);
  return mapped;
}

export async function logout(): Promise<void> {
  try {
    await fetchVoid(`${API_BASE}/auth/logout`, { method: 'POST' });
  } finally {
    clearStoredUser();
  }
}

export async function verifySession(): Promise<AuthUser | null> {
  try {
    const response = await fetchJson<AuthResponseSchema>(`${API_BASE}/auth/me`);
    const user = mapAuthResponse(response);
    setStoredUser(user);
    return user;
  } catch (error) {
    const apiError = toUnknownApiError(error);

    if (apiError.status === 401 || apiError.status === 403) {
      clearStoredUser();
      return null;
    }

    // Transient startup/proxy/network failures should not force logout.
    return getStoredUser();
  }
}
