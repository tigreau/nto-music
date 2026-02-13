package com.musicshop.controller.user;

import com.musicshop.model.user.Notification;
import com.musicshop.model.user.User;
import com.musicshop.repository.user.NotificationRepository;
import com.musicshop.repository.user.UserRepository;
import com.musicshop.service.notification.NotificationSseService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationSseService sseService;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationController(
            NotificationSseService sseService,
            NotificationRepository notificationRepository,
            UserRepository userRepository) {
        this.sseService = sseService;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /**
     * SSE endpoint - establishes real-time connection
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return sseService.createEmitter(user.getId());
    }

    /**
     * Get notification history (for initial load / offline periods)
     */
    @GetMapping
    public List<Notification> getNotifications(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepository.findByUserOrderByTimestampDesc(user);
    }

    /**
     * Mark notification as read
     */
    @PatchMapping("/{notificationId}/read")
    public void markAsRead(@PathVariable Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    /**
     * Mark all as read
     */
    @PatchMapping("/read-all")
    public void markAllAsRead(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Notification> unread = notificationRepository.findByUserAndIsReadFalse(user);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    /**
     * Delete notification
     */
    @DeleteMapping("/{notificationId}")
    public void deleteNotification(@PathVariable Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
