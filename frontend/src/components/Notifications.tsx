import { useState, useEffect } from 'react';
import { X, Bell } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { useNavigate } from 'react-router-dom';
import { useCart } from "@/context/CartContext";

interface Notification {
    id: number;
    message: string;
    timestamp: string;
}

const Notifications = () => {
    const [notifications, setNotifications] = useState<Notification[]>([]);
    const navigate = useNavigate();
    const { refreshCart } = useCart();

    useEffect(() => {
        // Poll for notifications every 3 seconds
        const fetchNotifications = () => {
            console.log("Checking for notifications...");
            // Hardcoded user ID 1 for now, similar to cart
            fetch('/api/notifications/user/1')
                .then(res => {
                    if (res.ok) return res.json();
                    return [];
                })
                .then(data => {
                    if (Array.isArray(data)) {
                        // If we have new notifications (or just any notifications), refresh the cart
                        // In a real app, we'd diff the lists, but refreshing on every poll with data is okay for now
                        // or better: check if data length > previous length
                        // For simplicity, if we receive any notifications, we refresh the cart config
                        // knowing that notifications are often about cart updates.
                        if (data.length > 0) {
                            refreshCart();
                        }
                        console.log("Notifications received:", data);
                        setNotifications(data);
                    }
                })
                .catch(err => console.error("Failed to fetch notifications", err));
        };

        fetchNotifications();
        const interval = setInterval(fetchNotifications, 3000);
        return () => clearInterval(interval);
    }, []); // Removed refreshCart dependnecy to avoid loop if it changes often, though it shouldn't.

    const dismissNotification = (e: React.MouseEvent, id: number) => {
        e.stopPropagation(); // Prevent triggering the card click
        fetch(`/api/notifications/${id}`, { method: 'DELETE' })
            .then(res => {
                if (res.ok) {
                    setNotifications(prev => prev.filter(n => n.id !== id));
                }
            })
            .catch(err => console.error("Failed to delete notification", err));
    };

    const handleNotificationClick = () => {
        navigate('/cart');
    };

    if (notifications.length === 0) return null;

    return (
        <div className="fixed bottom-4 right-4 z-50 flex flex-col gap-2 max-w-md w-full px-4">
            {notifications.map(notification => (
                <div
                    key={notification.id}
                    className="bg-white dark:bg-zinc-950 border border-border shadow-xl rounded-lg p-4 flex items-start gap-3 animate-in slide-in-from-right cursor-pointer hover:bg-zinc-50 dark:hover:bg-zinc-900 transition-colors"
                    onClick={handleNotificationClick}
                >
                    <div className="bg-primary/10 p-2 rounded-full flex-shrink-0">
                        <Bell className="w-4 h-4 text-primary" />
                    </div>
                    <div className="flex-1">
                        <p className="text-sm font-medium text-foreground">{notification.message}</p>
                        <p className="text-xs text-muted-foreground mt-1">
                            {new Date(notification.timestamp).toLocaleString()}
                        </p>
                    </div>
                    <Button
                        variant="ghost"
                        size="icon"
                        className="h-6 w-6 -mt-1 -mr-1"
                        onClick={(e) => dismissNotification(e, notification.id)}
                    >
                        <X className="w-4 h-4" />
                    </Button>
                </div>
            ))}
        </div>
    );
};

export default Notifications;
