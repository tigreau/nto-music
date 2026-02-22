package com.musicshop.controller.user;

import com.musicshop.application.notification.NotificationUseCase;
import com.musicshop.dto.user.NotificationDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationUseCase notificationUseCase;

    public NotificationController(NotificationUseCase notificationUseCase) {
        this.notificationUseCase = notificationUseCase;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("isAuthenticated()")
    public Object streamNotifications(Authentication authentication) {
        return notificationUseCase.openStream(authentication.getName());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<NotificationDTO> getNotifications(Authentication authentication) {
        return notificationUseCase.getNotifications(authentication.getName());
    }

    @PatchMapping("/{notificationId}/read")
    @PreAuthorize("@accessGuard.canAccessNotification(#notificationId, authentication)")
    @Operation(summary = "Mark notification as read")
    @ApiResponse(responseCode = "204", description = "No Content")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationUseCase.markAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark all notifications as read")
    @ApiResponse(responseCode = "204", description = "No Content")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        notificationUseCase.markAllAsRead(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{notificationId}")
    @PreAuthorize("@accessGuard.canAccessNotification(#notificationId, authentication)")
    @Operation(summary = "Delete notification")
    @ApiResponse(responseCode = "204", description = "No Content")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        notificationUseCase.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }
}
