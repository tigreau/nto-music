package com.musicshop.security;

import com.musicshop.model.cart.Cart;
import com.musicshop.model.cart.CartDetail;
import com.musicshop.model.user.Notification;
import com.musicshop.model.user.User;
import com.musicshop.repository.cart.CartDetailRepository;
import com.musicshop.repository.user.NotificationRepository;
import com.musicshop.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessGuardTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartDetailRepository cartDetailRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private Authentication authentication;

    private AccessGuard accessGuard;

    @BeforeEach
    void setUp() {
        this.accessGuard = new AccessGuard(userRepository, cartDetailRepository, notificationRepository);
    }

    @Test
    void canAccessUser_allowsAdminRegardlessOfOwner() {
        Collection<? extends GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(authentication).getAuthorities();

        boolean allowed = accessGuard.canAccessUser(99L, authentication);

        assertThat(allowed).isTrue();
    }

    @Test
    void canAccessUser_allowsOwner() {
        User user = new User();
        user.setEmail("owner@musicshop.com");

        doReturn(Collections.emptyList()).when(authentication).getAuthorities();
        when(authentication.getName()).thenReturn("owner@musicshop.com");
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        boolean allowed = accessGuard.canAccessUser(10L, authentication);

        assertThat(allowed).isTrue();
    }

    @Test
    void canAccessUser_deniesDifferentUser() {
        User user = new User();
        user.setEmail("owner@musicshop.com");

        doReturn(Collections.emptyList()).when(authentication).getAuthorities();
        when(authentication.getName()).thenReturn("other@musicshop.com");
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        boolean allowed = accessGuard.canAccessUser(10L, authentication);

        assertThat(allowed).isFalse();
    }

    @Test
    void canAccessCartDetail_allowsOwner() {
        User user = new User();
        user.setEmail("owner@musicshop.com");

        Cart cart = new Cart();
        cart.setUser(user);

        CartDetail detail = new CartDetail();
        detail.setCart(cart);

        doReturn(Collections.emptyList()).when(authentication).getAuthorities();
        when(authentication.getName()).thenReturn("owner@musicshop.com");
        when(cartDetailRepository.findById(20L)).thenReturn(Optional.of(detail));

        boolean allowed = accessGuard.canAccessCartDetail(20L, authentication);

        assertThat(allowed).isTrue();
    }

    @Test
    void canAccessCartDetail_deniesNonOwner() {
        User user = new User();
        user.setEmail("owner@musicshop.com");

        Cart cart = new Cart();
        cart.setUser(user);

        CartDetail detail = new CartDetail();
        detail.setCart(cart);

        doReturn(Collections.emptyList()).when(authentication).getAuthorities();
        when(authentication.getName()).thenReturn("other@musicshop.com");
        when(cartDetailRepository.findById(20L)).thenReturn(Optional.of(detail));

        boolean allowed = accessGuard.canAccessCartDetail(20L, authentication);

        assertThat(allowed).isFalse();
    }

    @Test
    void canAccessNotification_allowsOwner() {
        User user = new User();
        user.setEmail("owner@musicshop.com");

        Notification notification = new Notification();
        notification.setUser(user);

        doReturn(Collections.emptyList()).when(authentication).getAuthorities();
        when(authentication.getName()).thenReturn("owner@musicshop.com");
        when(notificationRepository.findById(30L)).thenReturn(Optional.of(notification));

        boolean allowed = accessGuard.canAccessNotification(30L, authentication);

        assertThat(allowed).isTrue();
    }

    @Test
    void canAccessNotification_deniesNonOwner() {
        User user = new User();
        user.setEmail("owner@musicshop.com");

        Notification notification = new Notification();
        notification.setUser(user);

        doReturn(Collections.emptyList()).when(authentication).getAuthorities();
        when(authentication.getName()).thenReturn("other@musicshop.com");
        when(notificationRepository.findById(30L)).thenReturn(Optional.of(notification));

        boolean allowed = accessGuard.canAccessNotification(30L, authentication);

        assertThat(allowed).isFalse();
    }
}
