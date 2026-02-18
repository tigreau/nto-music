import { AuthUser } from '@/types';

const AUTH_STORAGE_KEY = 'user';

function isAuthUser(value: unknown): value is AuthUser {
    if (!value || typeof value !== 'object') {
        return false;
    }

    const candidate = value as Partial<AuthUser>;
    return (
        typeof candidate.userId === 'number' &&
        typeof candidate.email === 'string' &&
        typeof candidate.firstName === 'string' &&
        typeof candidate.role === 'string' &&
        (candidate.lastName === undefined || typeof candidate.lastName === 'string')
    );
}

export function readStoredUser(): AuthUser | null {
    const raw = localStorage.getItem(AUTH_STORAGE_KEY);
    if (!raw) return null;

    try {
        const parsed: unknown = JSON.parse(raw);
        if (!isAuthUser(parsed)) {
            localStorage.removeItem(AUTH_STORAGE_KEY);
            return null;
        }
        return parsed;
    } catch {
        localStorage.removeItem(AUTH_STORAGE_KEY);
        return null;
    }
}

export function writeStoredUser(user: AuthUser): void {
    localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(user));
}

export function clearStoredUser(): void {
    localStorage.removeItem(AUTH_STORAGE_KEY);
}
