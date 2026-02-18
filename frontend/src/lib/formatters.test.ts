import { describe, expect, it } from 'vitest';
import { formatRelativeTime, formatUserDisplayName } from '@/lib/formatters';

describe('formatters', () => {
  it('formats user display names', () => {
    expect(formatUserDisplayName('Chris', 'Pine')).toBe('Chris P.');
    expect(formatUserDisplayName(undefined, undefined)).toBe('Profile');
  });

  it('formats relative time consistently', () => {
    const now = new Date('2026-02-17T12:00:00.000Z');
    expect(formatRelativeTime('2026-02-17T11:59:40.000Z', now)).toBe('just now');
    expect(formatRelativeTime('2026-02-17T11:30:00.000Z', now)).toContain('minute');
    expect(formatRelativeTime('2026-02-16T12:00:00.000Z', now)).toContain('day');
  });
});
