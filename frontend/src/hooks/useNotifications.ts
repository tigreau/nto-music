import { useState, useEffect, useCallback } from 'react';
import {
    fetchJson,
    markNotificationRead,
    markAllNotificationsRead,
    deleteNotification as deleteNotificationRequest,
} from '@/api/client';
import { createNotificationsEventSource } from '@/lib/notificationsStream';

export interface Notification {
    id: number;
    message: string;
    type: 'PRODUCT_UPDATE' | 'PRICE_DROP' | 'BACK_IN_STOCK' | 'ORDER_CONFIRMED' | 'ORDER_SHIPPED' | 'CART_REMINDER' | 'WISHLIST_SALE';
    timestamp: string;
    read: boolean;
    relatedEntityId?: number;
}

export function useNotifications(isAuthenticated: boolean) {
    const [notifications, setNotifications] = useState<Notification[]>([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [connectionStatus, setConnectionStatus] = useState<'connecting' | 'connected' | 'disconnected'>('disconnected');

    // Load initial history
    useEffect(() => {
        if (!isAuthenticated) {
            setNotifications([]);
            setUnreadCount(0);
            return;
        }

        fetchJson<Notification[]>('/api/notifications')
            .then((data: Notification[]) => {
                setNotifications(data);
                setUnreadCount(data.filter((n: Notification) => !n.read).length);
            })
            .catch((err: unknown) => console.error("Failed to load notifications", err));

    }, [isAuthenticated]);

    // Setup SSE
    useEffect(() => {
        if (!isAuthenticated) {
            setConnectionStatus('disconnected');
            return;
        }

        setConnectionStatus('connecting');

        // We need to pass credentials (cookies) to EventSource.
        // Standard EventSource doesn't support 'credentials: include' easily in some browsers?
        // Actually, withConnect (native EventSource), it usually sends cookies if same origin?
        // Since we are proxying, it might be fine.
        // Chrome/Firefox send cookies for same-origin EventSource.
        // Backend is /api/notifications/stream. Frontend is / (proxied to api).
        // It should work.

        const eventSource = createNotificationsEventSource('/api/notifications/stream');

        eventSource.onopen = () => {
            console.log("SSE Connected");
            setConnectionStatus('connected');
        };

        eventSource.addEventListener('notification', (event: MessageEvent) => {
            try {
                const newNotification: Notification = JSON.parse(event.data);
                setNotifications(prev => [newNotification, ...prev]);
                setUnreadCount(prev => prev + 1);
            } catch (e) {
                console.error("Error parsing notification event", e);
            }
        });

        eventSource.addEventListener('connected', (event: MessageEvent) => {
            console.log("SSE Handshake:", event.data);
        });

        eventSource.onerror = (err) => {
            console.error("SSE Error", err);
            // EventSource auto-reconnects, but if 401/403, we should close.
            if (eventSource.readyState === EventSource.CLOSED) {
                setConnectionStatus('disconnected');
            }
        };

        return () => {
            eventSource.close();
        };
    }, [isAuthenticated]);

    const markAsRead = useCallback(async (id: number) => {
        try {
            await markNotificationRead(id);

            setNotifications(prev => prev.map(n =>
                n.id === id ? { ...n, read: true } : n
            ));
            setUnreadCount(prev => Math.max(0, prev - 1));
        } catch (e) {
            console.error("Failed to mark as read", e);
        }
    }, []);

    const markAllAsRead = useCallback(async () => {
        try {
            await markAllNotificationsRead();
            setNotifications(prev => prev.map(n => ({ ...n, read: true })));
            setUnreadCount(0);
        } catch (e) {
            console.error("Failed to mark all as read", e);
        }
    }, []);

    const deleteNotification = useCallback(async (id: number) => {
        try {
            await deleteNotificationRequest(id);

            setNotifications(prev => {
                const target = prev.find(n => n.id === id);
                if (target && !target.read) {
                    setUnreadCount(c => Math.max(0, c - 1));
                }
                return prev.filter(n => n.id !== id);
            });
        } catch (e) {
            console.error("Failed to delete notification", e);
        }
    }, []);

    return {
        notifications,
        unreadCount,
        connectionStatus,
        markAsRead,
        markAllAsRead,
        deleteNotification
    };
}
