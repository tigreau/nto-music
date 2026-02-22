package com.musicshop.service.notification;

import com.musicshop.model.user.Notification;

public interface NotificationBroker {

    Object open(Long userId);

    void sendToUser(Long userId, Notification notification);

    void close(Long userId);
}
