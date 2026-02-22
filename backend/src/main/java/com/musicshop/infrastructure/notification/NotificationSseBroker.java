package com.musicshop.infrastructure.notification;

import com.musicshop.model.user.Notification;
import com.musicshop.service.notification.NotificationBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationSseBroker implements NotificationBroker {

    private static final Logger logger = LoggerFactory.getLogger(NotificationSseBroker.class);
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L; // 30 minutes

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public Object open(Long userId) {
        close(userId);

        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((error) -> emitters.remove(userId));

        emitters.put(userId, emitter);

        try {
            emitter.send(SseEmitter.event().name("connected").data("Connected to notification stream"));
        } catch (IOException e) {
            logger.error("Error sending initial SSE event", e);
            emitters.remove(userId);
        }

        return emitter;
    }

    public void sendToUser(Long userId, Notification notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) {
            return;
        }

        try {
            emitter.send(SseEmitter.event().name("notification").data(notification));
        } catch (IOException e) {
            logger.error("Error sending notification to user {}", userId, e);
            emitters.remove(userId);
        }
    }

    public void close(Long userId) {
        SseEmitter emitter = emitters.remove(userId);
        if (emitter != null) {
            emitter.complete();
        }
    }
}
