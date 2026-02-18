# Frontend Architecture Commands

## Core quality gates

```bash
npm run lint
npm run lint:arch
npm run typecheck
npm run lint:all
```

## OpenAPI contract generation

Generate TypeScript API types from backend SpringDoc endpoint:

```bash
npm run api:types
```

Prerequisite:
- backend running locally on `http://localhost:8080` with `/v3/api-docs` accessible

Output:
- `src/types/generated/openapi.ts`

## Error handling standard

- API client throws typed `ApiError` from `src/lib/apiError.ts`.
- UI code should use `getErrorMessage(error)` for consistent fallback messaging.
