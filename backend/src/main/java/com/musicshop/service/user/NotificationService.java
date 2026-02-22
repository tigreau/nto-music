package com.musicshop.service.user;

import com.musicshop.event.product.ProductDeletionEvent;
import com.musicshop.event.product.ProductDiscountEvent;
import com.musicshop.dto.user.NotificationDTO;
import com.musicshop.exception.ResourceNotFoundException;
import com.musicshop.service.notification.NotificationBroker;
import com.musicshop.mapper.NotificationMapper;
import com.musicshop.model.product.Product;
import com.musicshop.event.product.ProductUpdateEvent;
import com.musicshop.model.cart.CartDetail;
import com.musicshop.model.user.Notification;
import com.musicshop.model.user.NotificationType;
import com.musicshop.model.user.User;
import com.musicshop.repository.cart.CartDetailRepository;
import com.musicshop.repository.user.NotificationRepository;
import com.musicshop.repository.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final CartDetailRepository cartDetailRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationBroker sseBroker;
    private final NotificationMapper notificationMapper;

    @Autowired
    public NotificationService(
            CartDetailRepository cartDetailRepository,
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            NotificationBroker sseBroker,
            NotificationMapper notificationMapper) {
        this.cartDetailRepository = cartDetailRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.sseBroker = sseBroker;
        this.notificationMapper = notificationMapper;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onProductUpdate(ProductUpdateEvent event) {
        Product updatedProduct = event.getUpdatedProduct();
        List<CartDetail> affectedCartDetails = cartDetailRepository.findByProductId(updatedProduct.getId());

        affectedCartDetails.forEach(cartDetail -> {
            User affectedUser = cartDetail.getCart().getUser();
            String message = String.format(
                    "Product '%s' in your cart was updated. New price: EUR %.2f",
                    updatedProduct.getName(),
                    updatedProduct.getPrice());

            createAndSendNotification(
                    affectedUser,
                    message,
                    NotificationType.PRODUCT_UPDATE,
                    updatedProduct.getId());
        });
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onProductDeletion(ProductDeletionEvent event) {
        Product deletedProduct = event.getDeletedProduct();
        List<CartDetail> affectedCartDetails = event.getAffectedCartDetails();

        affectedCartDetails.forEach(cartDetail -> {
            User affectedUser = cartDetail.getCart().getUser();
            String message = String.format(
                    "The product '%s' has been removed from your cart (no longer available).",
                    deletedProduct.getName());

            createAndSendNotification(
                    affectedUser,
                    message,
                    NotificationType.PRODUCT_UPDATE,
                    null);
        });
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onProductDiscount(ProductDiscountEvent event) {
        Product discountedProduct = event.getDiscountedProduct();
        BigDecimal originalPrice = event.getOriginalPrice();
        List<CartDetail> affectedCartDetails = cartDetailRepository.findByProductId(discountedProduct.getId());

        affectedCartDetails.forEach(cartDetail -> {
            User affectedUser = cartDetail.getCart().getUser();
            BigDecimal savedAmount = originalPrice.subtract(discountedProduct.getPrice());

            String message = String.format(
                    "Great news! '%s' in your cart is now EUR %.2f (save EUR %.2f)",
                    discountedProduct.getName(),
                    discountedProduct.getPrice(),
                    savedAmount);

            createAndSendNotification(
                    affectedUser,
                    message,
                    NotificationType.PRICE_DROP,
                    discountedProduct.getId());
        });
    }

    private void createAndSendNotification(
            User user,
            String message,
            NotificationType type,
            Long relatedEntityId) {

        try {
            Notification notification = new Notification();
            notification.setTimestamp(LocalDateTime.now());
            notification.setMessage(message);
            notification.setType(type);
            notification.setUser(user);
            notification.setRelatedEntityId(relatedEntityId);
            notification.setRead(false);

            Notification saved = notificationRepository.save(notification);
            sseBroker.sendToUser(user.getId(), saved);
        } catch (Exception e) {
            logger.error("Failed to send notification to user {}: {}", user.getId(), e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsForUser(User user) {
        return notificationRepository.findByUserOrderByTimestampDesc(user);
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationDTOsForUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByTimestampDesc(userId).stream()
                .map(notificationMapper::toNotificationDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> unread = notificationRepository.findByUserAndIsReadFalse(user);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    @Transactional
    public void markAllAsReadByUserId(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalse(userId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException("Notification not found");
        }
        notificationRepository.deleteById(notificationId);
    }

}
