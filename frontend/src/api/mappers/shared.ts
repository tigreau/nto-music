import type { ProductCondition } from '@/types';

export function requiredNumber(value: unknown, field: string): number {
  if (typeof value !== 'number' || Number.isNaN(value)) {
    throw new Error(`Invalid API response: ${field} must be a number`);
  }
  return value;
}

export function requiredString(value: unknown, field: string): string {
  if (typeof value !== 'string') {
    throw new Error(`Invalid API response: ${field} must be a string`);
  }
  return value;
}

export function optionalString(value: unknown): string | null {
  return typeof value === 'string' ? value : null;
}

export function asCondition(value: unknown, field: string): ProductCondition {
  switch (value) {
    case 'EXCELLENT':
    case 'GOOD':
    case 'FAIR':
      return value;
    default:
      throw new Error(`Invalid API response: ${field} has unsupported condition value`);
  }
}
