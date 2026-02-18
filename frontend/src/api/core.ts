import { toApiError } from '@/lib/apiError';
import type { operations } from '@/types/generated/openapi';

export const API_BASE = '/api';

type ExtractContent<T> = T extends Record<string, infer V> ? V : never;
type ResponseContent<T> = T extends { content: infer C } ? ExtractContent<C> : void;

export type OperationName = keyof operations;

export type OperationParameters<Op extends OperationName> = operations[Op]['parameters'];

export type OperationQuery<Op extends OperationName> =
  OperationParameters<Op> extends { query?: infer Q } ? Q : never;

export type OperationPath<Op extends OperationName> =
  OperationParameters<Op> extends { path?: infer P } ? P : never;

export type OperationRequestBody<Op extends OperationName> =
  operations[Op] extends { requestBody: { content: infer C } } ? ExtractContent<C> : never;

export type OperationResponse<Op extends OperationName> =
  operations[Op]['responses'] extends infer Res
    ? (200 extends keyof Res ? ResponseContent<Res[200]> : never)
      | (201 extends keyof Res ? ResponseContent<Res[201]> : never)
      | (202 extends keyof Res ? ResponseContent<Res[202]> : never)
      | (204 extends keyof Res ? ResponseContent<Res[204]> : never)
    : never;

async function buildApiError(response: Response) {
  let payload: unknown;
  try {
    payload = await response.json();
  } catch {
    payload = undefined;
  }
  return toApiError(response, payload);
}

async function fetchWithCredentials(url: string, options?: RequestInit): Promise<Response> {
  const headers: Record<string, string> = {
    ...((options?.headers as Record<string, string>) || {}),
  };

  const response = await fetch(url, {
    ...options,
    headers,
    credentials: 'include',
  });

  if (!response.ok) {
    throw await buildApiError(response);
  }

  return response;
}

export async function fetchJson<T>(url: string, options?: RequestInit): Promise<T> {
  const response = await fetchWithCredentials(url, options);
  if (response.status === 204 || response.status === 205) {
    return undefined as T;
  }

  const raw = await response.text();
  if (!raw.trim()) {
    return undefined as T;
  }

  return JSON.parse(raw) as T;
}

export async function fetchVoid(url: string, options?: RequestInit): Promise<void> {
  await fetchWithCredentials(url, options);
}

export async function postJson<T>(url: string, body: unknown): Promise<T> {
  return fetchJson<T>(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
}

export async function putJson<T>(url: string, body: unknown): Promise<T> {
  return fetchJson<T>(url, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
}

export async function patchJson<T>(url: string, body?: unknown): Promise<T> {
  const options: RequestInit = {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
  };

  if (body !== undefined) {
    options.body = JSON.stringify(body);
  }

  return fetchJson<T>(url, options);
}
