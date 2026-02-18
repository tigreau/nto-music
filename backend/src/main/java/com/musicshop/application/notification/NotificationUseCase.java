package com.musicshop.application.notification;

import com.musicshop.dto.user.NotificationDTO;
import com.musicshop.model.user.Notification;
import com.musicshop.model.user.User;
import org.springframework.security.access.prepost.PreAuthorize;
import com.musicshop.service.notification.NotificationSseService;
import com.musicshop.service.user.NotificationService;
import com.musicshop.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationUseCase {

    private final NotificationSseService sseService;
    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationUseCase(
            NotificationSseService sseService,
            NotificationService notificationService,
            UserService userService) {
        this.sseService = sseService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @PreAuthorize("isAuthenticated()")
    public SseEmitter stream(String email) {
        User user = userService.findByEmail(email);
        return sseService.createEmitter(user.getId());
    }

    @PreAuthorize("isAuthenticated()")
    public List<NotificationDTO> getNotifications(String email) {
        User user = userService.findByEmail(email);
        return notificationService.getNotificationsForUser(user).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("isAuthenticated()")
    public void markAsRead(Long notificationId) {
        notificationService.markAsRead(notificationId);
    }

    @PreAuthorize("isAuthenticated()")
    public void markAllAsRead(String email) {
        User user = userService.findByEmail(email);
        notificationService.markAllAsRead(user);
    }

    @PreAuthorize("isAuthenticated()")
    public void deleteNotification(Long notificationId) {
        notificationService.deleteNotification(notificationId);
    }

    private NotificationDTO toDto(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType().name());
        dto.setTimestamp(notification.getTimestamp().toString());
        dto.setRead(notification.isRead());
        dto.setRelatedEntityId(notification.getRelatedEntityId());
        return dto;
    }
}
