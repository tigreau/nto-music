package com.musicshop.controller.user;

import com.musicshop.model.user.Notification;
import com.musicshop.model.user.User;
import com.musicshop.service.notification.NotificationSseService;
import com.musicshop.service.user.NotificationService;
import com.musicshop.service.user.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationSseService sseService;
    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(
            NotificationSseService sseService,
            NotificationService notificationService,
            UserService userService) {
        this.sseService = sseService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        return sseService.createEmitter(user.getId());
    }

    @GetMapping
    public List<Notification> getNotifications(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        return notificationService.getNotificationsForUser(user);
    }

    @PatchMapping("/{notificationId}/read")
    public void markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
    }

    @PatchMapping("/read-all")
    public void markAllAsRead(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        notificationService.markAllAsRead(user);
    }

    @DeleteMapping("/{notificationId}")
    public void deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
    }
}
