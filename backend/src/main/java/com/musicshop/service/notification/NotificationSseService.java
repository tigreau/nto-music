package com.musicshop.service.notification;

import com.musicshop.model.user.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationSseService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationSseService.class);
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L; // 30 minutes

    // Store active SSE connections: userId -> SseEmitter
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public NotificationSseService() {
    }

    /**
     * Create SSE connection for a user
     */
    public SseEmitter createEmitter(Long userId) {
        // Remove old emitter if exists
        removeEmitter(userId);

        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        // Setup callbacks
        emitter.onCompletion(() -> {
            logger.debug("SSE completed for user {}", userId);
            emitters.remove(userId);
        });

        emitter.onTimeout(() -> {
            logger.debug("SSE timeout for user {}", userId);
            emitters.remove(userId);
        });

        emitter.onError((error) -> {
            logger.error("SSE error for user {}", userId, error);
            emitters.remove(userId);
        });

        emitters.put(userId, emitter);
        logger.debug("SSE connection created for user {}", userId);

        // Send initial "connected" event
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Connected to notification stream"));
        } catch (IOException e) {
            logger.error("Error sending initial event", e);
            emitters.remove(userId);
        }

        return emitter;
    }

    /**
     * Send notification to specific user
     */
    public void sendToUser(Long userId, Notification notification) {
        SseEmitter emitter = emitters.get(userId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notification));
                logger.debug("Sent notification to user {}: {}", userId, notification.getMessage());
            } catch (IOException e) {
                logger.error("Error sending notification to user {}", userId, e);
                emitters.remove(userId);
            }
        } else {
            logger.debug("No active SSE connection for user {}", userId);
        }
    }

    /**
     * Broadcast notification to all connected users
     */
    public void broadcast(Notification notification) {
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("broadcast")
                        .data(notification));
            } catch (IOException e) {
                logger.error("Error broadcasting to user {}", userId, e);
                emitters.remove(userId);
            }
        });
    }

    /**
     * Remove emitter for user
     */
    public void removeEmitter(Long userId) {
        SseEmitter emitter = emitters.remove(userId);
        if (emitter != null) {
            emitter.complete();
        }
    }

    /**
     * Get count of active connections
     */
    public int getActiveConnectionCount() {
        return emitters.size();
    }
}
