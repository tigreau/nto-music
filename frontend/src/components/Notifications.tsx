import React from 'react';
import { Bell, Check, Trash2, Loader2 } from 'lucide-react';
import { useNotifications } from '@/hooks/useNotifications';
import { Button } from '@/components/ui/button';
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { cn } from '@/lib/utils'; // Assuming utils exists (standard shadcn/ui)
import { formatRelativeTime } from '@/lib/formatters';

interface NotificationsProps {
    isAuthenticated: boolean;
}

export const Notifications: React.FC<NotificationsProps> = ({ isAuthenticated }) => {
    const {
        notifications,
        unreadCount,
        connectionStatus,
        markAsRead,
        markAllAsRead,
        deleteNotification
    } = useNotifications(isAuthenticated);

    if (!isAuthenticated) return null;

    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="icon" className="relative text-white/80 hover:text-white hover:bg-white/10">
                    <Bell className="w-5 h-5" />
                    {unreadCount > 0 && (
                        <span className="absolute -top-1 -right-1 w-5 h-5 bg-[#cb4b16] text-[#fdf6e3] text-xs rounded-full flex items-center justify-center font-semibold pointer-events-none">
                            {unreadCount > 9 ? '9+' : unreadCount}
                        </span>
                    )}
                    <span className="sr-only">Notifications</span>
                </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="w-80 max-h-[500px] overflow-y-auto">
                <div className="flex items-center justify-between px-2 py-1.5 sticky top-0 bg-popover z-10 border-b">
                    <DropdownMenuLabel className="px-0 py-0">Notifications</DropdownMenuLabel>
                    <div className="flex gap-2 items-center">
                        {connectionStatus === 'connecting' && <Loader2 className="h-3 w-3 animate-spin text-muted-foreground title='Connecting...'" />}
                        {unreadCount > 0 && (
                            <Button
                                variant="ghost"
                                size="sm"
                                className="h-auto px-2 py-0.5 text-xs text-muted-foreground hover:text-primary"
                                onClick={(e) => {
                                    e.preventDefault();
                                    markAllAsRead();
                                }}
                            >
                                Mark all read
                            </Button>
                        )}
                    </div>
                </div>

                {notifications.length === 0 ? (
                    <div className="p-8 text-center text-sm text-muted-foreground">
                        No notifications
                    </div>
                ) : (
                    <div className="py-1">
                        {notifications.map(notification => (
                            <DropdownMenuItem
                                key={notification.id}
                                className={cn(
                                    "flex flex-col gap-1 p-3 cursor-default items-start focus:bg-accent focus:text-accent-foreground",
                                    !notification.read && "bg-accent/10 border-l-2 border-primary"
                                )}
                                onSelect={(e) => e.preventDefault()}
                            >
                                <div className="flex items-start justify-between gap-2 w-full">
                                    <p className={cn("leading-snug text-sm", !notification.read && "font-medium")}>
                                        {notification.message}
                                    </p>
                                    <div className="flex items-center gap-0.5 shrink-0 opacity-0 group-hover:opacity-100 transition-opacity">
                                        {/* Hover actions might be tricky in dropdown, so always show or show on hover of item? 
                                          DropdownMenuItem handles focus state. */}
                                    </div>
                                </div>

                                <div className="flex items-center justify-between w-full mt-1">
                                    <span className="text-[10px] text-muted-foreground">
                                        {formatRelativeTime(notification.timestamp)}
                                    </span>
                                    <div className="flex gap-1">
                                        {!notification.read && (
                                            <Button
                                                variant="ghost"
                                                size="icon"
                                                className="h-6 w-6 text-muted-foreground hover:text-primary"
                                                onClick={(e) => {
                                                    e.stopPropagation();
                                                    markAsRead(notification.id);
                                                }}
                                                title="Mark as read"
                                            >
                                                <Check className="h-3 w-3" />
                                                <span className="sr-only">Mark read</span>
                                            </Button>
                                        )}
                                        <Button
                                            variant="ghost"
                                            size="icon"
                                            className="h-6 w-6 text-muted-foreground hover:text-destructive"
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                deleteNotification(notification.id);
                                            }}
                                            title="Delete"
                                        >
                                            <Trash2 className="h-3 w-3" />
                                            <span className="sr-only">Delete</span>
                                        </Button>
                                    </div>
                                </div>
                            </DropdownMenuItem>
                        ))}
                    </div>
                )}
            </DropdownMenuContent>
        </DropdownMenu>
    );
};
