package com.musicshop.service.user;

import com.musicshop.event.product.ProductDeletionEvent;
import com.musicshop.event.product.ProductDiscountEvent;
import com.musicshop.model.product.Product;
import com.musicshop.event.product.ProductUpdateEvent;
import com.musicshop.model.cart.CartDetail;
import com.musicshop.model.user.Notification;
import com.musicshop.model.user.NotificationType;
import com.musicshop.model.user.User;
import com.musicshop.repository.cart.CartDetailRepository;
import com.musicshop.repository.user.NotificationRepository;
import com.musicshop.repository.user.UserRepository;
import com.musicshop.service.notification.NotificationSseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final CartDetailRepository cartDetailRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationSseService sseService;

    @Autowired
    public NotificationService(
            CartDetailRepository cartDetailRepository,
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            NotificationSseService sseService) {
        this.cartDetailRepository = cartDetailRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.sseService = sseService;
    }

    @EventListener
    @Transactional
    public void onProductUpdate(ProductUpdateEvent event) {
        Product updatedProduct = event.getUpdatedProduct();
        List<CartDetail> affectedCartDetails = cartDetailRepository.findByProductId(updatedProduct.getId());

        affectedCartDetails.forEach(cartDetail -> {
            User affectedUser = cartDetail.getCart().getUser();
            String message = String.format(
                    "Product '%s' in your cart was updated. New price: €%.2f",
                    updatedProduct.getName(),
                    updatedProduct.getPrice());

            createAndSendNotification(
                    affectedUser,
                    message,
                    NotificationType.PRODUCT_UPDATE,
                    updatedProduct.getId());
        });
    }

    @EventListener
    @Transactional
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

    @EventListener
    @Transactional
    public void onProductDiscount(ProductDiscountEvent event) {
        Product discountedProduct = event.getDiscountedProduct();
        BigDecimal originalPrice = event.getOriginalPrice();
        List<CartDetail> affectedCartDetails = cartDetailRepository.findByProductId(discountedProduct.getId());

        affectedCartDetails.forEach(cartDetail -> {
            User affectedUser = cartDetail.getCart().getUser();
            BigDecimal savedAmount = originalPrice.subtract(discountedProduct.getPrice());

            String message = String.format(
                    "Great news! '%s' in your cart is now €%.2f (save €%.2f)",
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

            // Save to database
            Notification saved = notificationRepository.save(notification);

            // Push to user in real-time (if connected)
            sseService.sendToUser(user.getId(), saved);

        } catch (Exception e) {
            // Log but don't fail the transaction
            System.err.println("Failed to send notification to user " + user.getId() + ": " + e.getMessage());
        }
    }

    public List<Notification> getNotificationsForUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(notificationRepository::findByUserOrderByTimestampDesc).orElseGet(List::of);
    }

    public boolean deleteNotification(Long notificationId) {
        if (notificationRepository.existsById(notificationId)) {
            notificationRepository.deleteById(notificationId);
            return true;
        }
        return false;
    }
}
