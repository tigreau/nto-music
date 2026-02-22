package com.musicshop.service.checkout;

import com.musicshop.service.notification.NotificationBroker;
import com.musicshop.model.order.UserOrder;
import com.musicshop.model.user.Notification;
import com.musicshop.model.user.NotificationType;
import com.musicshop.model.user.User;
import com.musicshop.repository.user.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CheckoutNotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationBroker notificationBroker;

    public CheckoutNotificationService(NotificationRepository notificationRepository,
            NotificationBroker notificationBroker) {
        this.notificationRepository = notificationRepository;
        this.notificationBroker = notificationBroker;
    }

    public void sendOrderConfirmation(User user, UserOrder order) {
        Notification notification = new Notification();
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(
                String.format("Order #%d confirmed! Total: EUR %.2f", order.getId(), order.getTotalAmount()));
        notification.setType(NotificationType.ORDER_CONFIRMED);
        notification.setUser(user);
        notification.setRelatedEntityId(order.getId());
        notification.setRead(false);

        Notification saved = notificationRepository.save(notification);
        notificationBroker.sendToUser(user.getId(), saved);
    }
}
