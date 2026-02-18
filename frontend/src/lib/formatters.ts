export function formatRelativeTime(dateString: string, now = new Date()): string {
    const date = new Date(dateString);
    const diffInSeconds = (date.getTime() - now.getTime()) / 1000;
    const formatter = new Intl.RelativeTimeFormat('en', { numeric: 'auto' });

    if (diffInSeconds > -60) return 'just now';
    if (diffInSeconds > -3600) return formatter.format(Math.round(diffInSeconds / 60), 'minute');
    if (diffInSeconds > -86400) return formatter.format(Math.round(diffInSeconds / 3600), 'hour');
    return formatter.format(Math.round(diffInSeconds / 86400), 'day');
}

export function formatUserDisplayName(firstName?: string, lastName?: string): string {
    const safeFirstName = firstName || 'Profile';
    const lastNameInitial = lastName ? `${lastName[0]}.` : '';
    return `${safeFirstName} ${lastNameInitial}`.trim();
}
