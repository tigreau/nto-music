package com.musicshop.data.seeder;

import com.musicshop.model.cart.Cart;
import com.musicshop.model.user.User;
import com.musicshop.model.user.UserRole;
import com.musicshop.repository.cart.CartRepository;
import com.musicshop.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class UserSeeder implements DataSeeder {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(UserRepository userRepository,
            CartRepository cartRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void seed() {
        if (userRepository.count() > 0) {
            return;
        }

        // Customer user
        User customer = new User();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhoneNumber("1234567890");
        customer.setPassword(passwordEncoder.encode("password123"));
        customer.setRole(UserRole.CUSTOMER);
        userRepository.save(customer);

        Cart customerCart = new Cart();
        customerCart.setUser(customer);
        customerCart.setDateCreated(LocalDateTime.now());
        cartRepository.save(customerCart);

        // Admin user
        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEmail("admin@musicshop.com");
        admin.setPhoneNumber("0987654321");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);

        Cart adminCart = new Cart();
        adminCart.setUser(admin);
        adminCart.setDateCreated(LocalDateTime.now());
        cartRepository.save(adminCart);

        // Second customer user (for testing cart isolation)
        User customer2 = new User();
        customer2.setFirstName("Jane");
        customer2.setLastName("Smith");
        customer2.setEmail("jane.smith@example.com");
        customer2.setPhoneNumber("5551234567");
        customer2.setPassword(passwordEncoder.encode("password123"));
        customer2.setRole(UserRole.CUSTOMER);
        userRepository.save(customer2);

        Cart customer2Cart = new Cart();
        customer2Cart.setUser(customer2);
        customer2Cart.setDateCreated(LocalDateTime.now());
        cartRepository.save(customer2Cart);
    }

    public User getDefaultUser() {
        return userRepository.findAll().stream().findFirst().orElse(null);
    }
}
