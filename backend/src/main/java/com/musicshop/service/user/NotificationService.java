package com.musicshop.service.user;

import com.musicshop.event.product.ProductDeletionEvent;
import com.musicshop.event.product.ProductDiscountEvent;
import com.musicshop.model.product.Product;
import com.musicshop.event.product.ProductUpdateEvent;
import com.musicshop.model.cart.CartDetail;
import com.musicshop.model.user.Notification;
import com.musicshop.model.user.User;
import com.musicshop.repository.cart.CartDetailRepository;
import com.musicshop.repository.user.NotificationRepository;
import com.musicshop.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final CartDetailRepository cartDetailRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Autowired
    public NotificationService(CartDetailRepository cartDetailRepository, NotificationRepository notificationRepository,
            UserRepository userRepository) {
        this.cartDetailRepository = cartDetailRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @EventListener
    public void onProductUpdate(ProductUpdateEvent event) {
        Product updatedProduct = event.getUpdatedProduct();
        Long updatedProductId = updatedProduct.getId();

        List<CartDetail> affectedCartDetails = cartDetailRepository.findByProductId(updatedProductId);

        affectedCartDetails.forEach(cartDetail -> {
            User affectedUser = cartDetail.getCart().getUser();
            String message = String.format("Product updated in cart: %s, Price: %s", updatedProduct.getName(),
                    updatedProduct.getPrice());
            createNotification(affectedUser, message);
        });
    }

    @EventListener
    public void onProductDeletion(ProductDeletionEvent event) {
        Product deletedProduct = event.getDeletedProduct();
        List<CartDetail> affectedCartDetails = event.getAffectedCartDetails();

        affectedCartDetails.forEach(cartDetail -> {
            User affectedUser = cartDetail.getCart().getUser();
            String message = "The product '" + deletedProduct.getName() + "' has been removed from your cart.";
            createNotification(affectedUser, message);
        });
    }

    @EventListener
    public void onProductDiscount(ProductDiscountEvent event) {
        Product discountedProduct = event.getDiscountedProduct();
        BigDecimal originalPrice = event.getOriginalPrice();
        Long discountedProductId = discountedProduct.getId();

        List<CartDetail> affectedCartDetails = cartDetailRepository.findByProductId(discountedProductId);

        affectedCartDetails.forEach(cartDetail -> {
            User affectedUser = cartDetail.getCart().getUser();
            String message = String.format("Price reduced for '%s' in your cart. Original Price: %s, New Price: %s",
                    discountedProduct.getName(), originalPrice, discountedProduct.getPrice());
            createNotification(affectedUser, message);
        });
    }

    private void createNotification(User user, String message) {
        Notification notification = new Notification();
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(message);
        notification.setUser(user);
        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(notificationRepository::findByUser).orElseGet(List::of);
    }

    public boolean deleteNotification(Long notificationId) {
        if (notificationRepository.existsById(notificationId)) {
            notificationRepository.deleteById(notificationId);
            return true;
        }
        return false;
    }
}
