package com.musicshop.security;

import com.musicshop.repository.cart.CartDetailRepository;
import com.musicshop.repository.user.NotificationRepository;
import com.musicshop.repository.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("accessGuard")
public class AccessGuard {

    private static final String ADMIN_AUTHORITY = "ROLE_ADMIN";

    private final UserRepository userRepository;
    private final CartDetailRepository cartDetailRepository;
    private final NotificationRepository notificationRepository;

    public AccessGuard(
            UserRepository userRepository,
            CartDetailRepository cartDetailRepository,
            NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.notificationRepository = notificationRepository;
    }

    public boolean canAccessUser(Long userId, Authentication authentication) {
        if (isAdmin(authentication)) {
            return true;
        }
        return userRepository.findById(userId)
                .map(user -> user.getEmail().equals(authentication.getName()))
                .orElse(false);
    }

    public boolean canAccessCartDetail(Long detailId, Authentication authentication) {
        if (isAdmin(authentication)) {
            return true;
        }
        return cartDetailRepository.findById(detailId)
                .map(detail -> detail.getCart().getUser().getEmail().equals(authentication.getName()))
                .orElse(false);
    }

    public boolean canAccessNotification(Long notificationId, Authentication authentication) {
        if (isAdmin(authentication)) {
            return true;
        }
        return notificationRepository.findById(notificationId)
                .map(notification -> notification.getUser().getEmail().equals(authentication.getName()))
                .orElse(false);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> ADMIN_AUTHORITY.equals(authority.getAuthority()));
    }
}
