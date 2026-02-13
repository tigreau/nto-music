import { ProductFilters, PageResponse, Product, DetailedProduct, Brand, Category, CategoryReviews } from '@/types';

const API_BASE = '/api';

// ─── User storage (non-sensitive display info only) ──────────────
export interface AuthUser {
    userId: number;
    email: string;
    firstName: string;
    role: string;
}

export interface AuthResponse {
    token: string | null;
    userId: number;
    email: string;
    firstName: string;
    role: string;
}

export function getStoredUser(): AuthUser | null {
    const raw = localStorage.getItem('user');
    return raw ? JSON.parse(raw) : null;
}

export function setStoredUser(user: AuthUser) {
    localStorage.setItem('user', JSON.stringify(user));
}

export function clearStoredUser() {
    localStorage.removeItem('user');
}

// ─── Core fetch (cookie-based auth) ─────────────────────────────
export async function fetchJson<T>(url: string, options?: RequestInit): Promise<T> {
    const headers: Record<string, string> = {
        ...(options?.headers as Record<string, string> || {}),
    };

    const response = await fetch(url, {
        ...options,
        headers,
        credentials: 'include',
    });
    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || `API error: ${response.status} ${response.statusText}`);
    }
    return response.json();
}

async function postJson<T>(url: string, body: unknown): Promise<T> {
    return fetchJson<T>(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
    });
}

// ─── Auth ────────────────────────────────────────────────────────
export async function login(email: string, password: string): Promise<AuthResponse> {
    const res = await postJson<AuthResponse>(`${API_BASE}/auth/login`, { email, password });
    setStoredUser({ userId: res.userId, email: res.email, firstName: res.firstName, role: res.role });
    return res;
}

export async function register(
    firstName: string, lastName: string, email: string, password: string
): Promise<AuthResponse> {
    const res = await postJson<AuthResponse>(`${API_BASE}/auth/register`, {
        firstName, lastName, email, password,
    });
    setStoredUser({ userId: res.userId, email: res.email, firstName: res.firstName, role: res.role });
    return res;
}

export async function logout(): Promise<void> {
    try {
        await fetch(`${API_BASE}/auth/logout`, {
            method: 'POST',
            credentials: 'include',
        });
    } finally {
        clearStoredUser();
    }
}

export async function verifySession(): Promise<AuthUser | null> {
    try {
        const res = await fetchJson<AuthResponse>(`${API_BASE}/auth/me`);
        const user = { userId: res.userId, email: res.email, firstName: res.firstName, role: res.role };
        setStoredUser(user);
        return user;
    } catch {
        clearStoredUser();
        return null;
    }
}

// ─── Products ────────────────────────────────────────────────────
export function fetchProducts(filters: ProductFilters = {}): Promise<PageResponse<Product>> {
    const params = new URLSearchParams();
    if (filters.category) params.set('category', filters.category);
    if (filters.brand) params.set('brand', filters.brand);
    if (filters.minPrice !== undefined) params.set('minPrice', String(filters.minPrice));
    if (filters.maxPrice !== undefined) params.set('maxPrice', String(filters.maxPrice));
    if (filters.condition) params.set('condition', filters.condition);
    if (filters.sort) params.set('sort', filters.sort);
    params.set('page', String(filters.page ?? 0));
    params.set('size', String(filters.size ?? 20));

    return fetchJson(`${API_BASE}/products?${params.toString()}`);
}

export function fetchProduct(id: number): Promise<DetailedProduct> {
    return fetchJson(`${API_BASE}/products/${id}`);
}

// ─── Categories ──────────────────────────────────────────────────
export function fetchCategories(): Promise<Category[]> {
    return fetchJson(`${API_BASE}/categories`);
}

export function fetchCategoryReviews(slug: string, page = 0, size = 10): Promise<CategoryReviews> {
    return fetchJson(`${API_BASE}/categories/${slug}/reviews?page=${page}&size=${size}`);
}

// ─── Brands ──────────────────────────────────────────────────────
export function fetchBrands(): Promise<Brand[]> {
    return fetchJson(`${API_BASE}/brands`);
}

