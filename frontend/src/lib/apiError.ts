export type ApiErrorCode =
    | 'RESOURCE_NOT_FOUND'
    | 'RESOURCE_IN_USE'
    | 'INSUFFICIENT_STOCK'
    | 'PAYMENT_FAILED'
    | 'CART_EMPTY'
    | 'DUPLICATE_RESOURCE'
    | 'VALIDATION_FAILED'
    | 'INVALID_ARGUMENT'
    | 'BAD_CREDENTIALS'
    | 'UNAUTHORIZED'
    | 'ACCESS_DENIED'
    | 'INTERNAL_ERROR'
    | 'UNKNOWN_ERROR';

export interface ApiErrorPayload {
    timestamp?: string;
    status?: number;
    error?: string;
    code?: string;
    message?: string;
}

export interface ApiErrorPolicy {
    message: string;
    action: 'none' | 'redirect_login';
}

export class ApiError extends Error {
    readonly status: number;
    readonly code: ApiErrorCode;
    readonly timestamp?: string;
    readonly reason?: string;

    constructor({
        message,
        status,
        code,
        timestamp,
        reason,
    }: {
        message: string;
        status: number;
        code: ApiErrorCode;
        timestamp?: string;
        reason?: string;
    }) {
        super(message);
        this.name = 'ApiError';
        this.status = status;
        this.code = code;
        this.timestamp = timestamp;
        this.reason = reason;
    }
}

function isObject(value: unknown): value is Record<string, unknown> {
    return typeof value === 'object' && value !== null;
}

function toApiErrorCode(value: unknown): ApiErrorCode {
    if (typeof value !== 'string') return 'UNKNOWN_ERROR';
    switch (value) {
        case 'RESOURCE_NOT_FOUND':
        case 'RESOURCE_IN_USE':
        case 'INSUFFICIENT_STOCK':
        case 'PAYMENT_FAILED':
        case 'CART_EMPTY':
        case 'DUPLICATE_RESOURCE':
        case 'VALIDATION_FAILED':
        case 'INVALID_ARGUMENT':
        case 'BAD_CREDENTIALS':
        case 'UNAUTHORIZED':
        case 'ACCESS_DENIED':
        case 'INTERNAL_ERROR':
            return value;
        default:
            return 'UNKNOWN_ERROR';
    }
}

export function toApiError(response: Response, payload?: unknown): ApiError {
    const safePayload: ApiErrorPayload = isObject(payload) ? payload as ApiErrorPayload : {};
    const fallbackMessage = `API error: ${response.status} ${response.statusText}`;
    return new ApiError({
        message: safePayload.message || fallbackMessage,
        status: safePayload.status ?? response.status,
        code: toApiErrorCode(safePayload.code),
        timestamp: safePayload.timestamp,
        reason: safePayload.error || response.statusText,
    });
}

export function toUnknownApiError(error: unknown): ApiError {
    if (error instanceof ApiError) return error;
    if (error instanceof Error) {
        return new ApiError({
            message: error.message,
            status: 0,
            code: 'UNKNOWN_ERROR',
        });
    }
    return new ApiError({
        message: 'An unexpected error occurred',
        status: 0,
        code: 'UNKNOWN_ERROR',
    });
}

export function getErrorMessage(error: unknown): string {
    return toUnknownApiError(error).message;
}

const ERROR_POLICY_MESSAGES: Partial<Record<ApiErrorCode, string>> = {
    RESOURCE_NOT_FOUND: 'The requested resource was not found.',
    INSUFFICIENT_STOCK: 'Some items are no longer available in the requested quantity.',
    PAYMENT_FAILED: 'Payment failed. Please try a different payment method.',
    CART_EMPTY: 'Your cart is empty.',
    DUPLICATE_RESOURCE: 'This item already exists.',
    VALIDATION_FAILED: 'Please review the highlighted form data.',
    INVALID_ARGUMENT: 'Some input values are invalid.',
    BAD_CREDENTIALS: 'Invalid email or password.',
    UNAUTHORIZED: 'Your session expired. Please sign in again.',
    ACCESS_DENIED: 'You do not have permission to perform this action.',
    INTERNAL_ERROR: 'Something went wrong on the server. Please try again.',
};

export function getApiErrorPolicy(error: unknown): ApiErrorPolicy {
    const apiError = toUnknownApiError(error);
    const fallbackMessage = apiError.message || 'An unexpected error occurred';
    const policyMessage =
        ((apiError.code === 'VALIDATION_FAILED'
            || apiError.code === 'INVALID_ARGUMENT'
            || apiError.code === 'RESOURCE_IN_USE')
            && apiError.message)
            ? apiError.message
            : ERROR_POLICY_MESSAGES[apiError.code];

    return {
        message: policyMessage || fallbackMessage,
        action: apiError.code === 'UNAUTHORIZED' ? 'redirect_login' : 'none',
    };
}
