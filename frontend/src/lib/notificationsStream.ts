export type NotificationsEventSource = EventSource;

export function createNotificationsEventSource(url: string): NotificationsEventSource {
    return new EventSource(url, { withCredentials: true });
}
