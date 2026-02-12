package com.musicshop.data.seeder;

import com.musicshop.model.cart.Cart;
import com.musicshop.model.user.User;
import com.musicshop.repository.cart.CartRepository;
import com.musicshop.repository.user.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class UserSeeder implements DataSeeder {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public UserSeeder(UserRepository userRepository, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    @Transactional
    public void seed() {
        if (userRepository.count() > 0) {
            return;
        }

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("1234567890");
        userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setDateCreated(LocalDateTime.now());
        cartRepository.save(cart);
    }

    public User getDefaultUser() {
        return userRepository.findAll().stream().findFirst().orElse(null);
    }
}
