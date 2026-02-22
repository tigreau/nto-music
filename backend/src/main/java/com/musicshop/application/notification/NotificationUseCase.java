package com.musicshop.application.notification;

import com.musicshop.dto.user.NotificationDTO;
import com.musicshop.service.notification.NotificationBroker;
import org.springframework.security.access.prepost.PreAuthorize;
import com.musicshop.service.user.NotificationService;
import com.musicshop.service.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationUseCase {

    private final NotificationService notificationService;
    private final UserService userService;
    private final NotificationBroker notificationBroker;

    public NotificationUseCase(
            NotificationService notificationService,
            UserService userService,
            NotificationBroker notificationBroker) {
        this.notificationService = notificationService;
        this.userService = userService;
        this.notificationBroker = notificationBroker;
    }

    @PreAuthorize("isAuthenticated()")
    public Long resolveUserId(String email) {
        return userService.findUserIdByEmail(email);
    }

    @PreAuthorize("isAuthenticated()")
    public Object openStream(String email) {
        Long userId = userService.findUserIdByEmail(email);
        return notificationBroker.open(userId);
    }

    @PreAuthorize("isAuthenticated()")
    public List<NotificationDTO> getNotifications(String email) {
        Long userId = userService.findUserIdByEmail(email);
        return notificationService.getNotificationDTOsForUserId(userId);
    }

    @PreAuthorize("isAuthenticated()")
    public void markAsRead(Long notificationId) {
        notificationService.markAsRead(notificationId);
    }

    @PreAuthorize("isAuthenticated()")
    public void markAllAsRead(String email) {
        Long userId = userService.findUserIdByEmail(email);
        notificationService.markAllAsReadByUserId(userId);
    }

    @PreAuthorize("isAuthenticated()")
    public void deleteNotification(Long notificationId) {
        notificationService.deleteNotification(notificationId);
    }
}
