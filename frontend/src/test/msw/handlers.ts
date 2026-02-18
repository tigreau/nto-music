import { HttpResponse, http } from 'msw';

export const handlers = [
  // Health/default endpoint to verify MSW is active in tests.
  http.get('/api/__msw_health', () => HttpResponse.json({ ok: true })),
];
