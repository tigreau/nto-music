package com.musicshop.data.seeder;

import com.musicshop.model.cart.Cart;
import com.musicshop.model.address.Address;
import com.musicshop.model.user.User;
import com.musicshop.model.user.UserAddress;
import com.musicshop.model.user.UserRole;
import com.musicshop.repository.address.AddressRepository;
import com.musicshop.repository.cart.CartDetailRepository;
import com.musicshop.repository.cart.CartRepository;
import com.musicshop.repository.user.UserAddressRepository;
import com.musicshop.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserSeeder implements DataSeeder {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final AddressRepository addressRepository;
    private final UserAddressRepository userAddressRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(UserRepository userRepository,
            CartRepository cartRepository,
            CartDetailRepository cartDetailRepository,
            AddressRepository addressRepository,
            UserAddressRepository userAddressRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.addressRepository = addressRepository;
        this.userAddressRepository = userAddressRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void seed() {
        upsertSeedUser(
                "John",
                "Doe",
                "john.doe@example.com",
                "1234567890",
                "password123",
                UserRole.CUSTOMER,
                "Main Street",
                "42A",
                "10001",
                "Amsterdam",
                "Netherlands");

        upsertSeedUser(
                "Admin",
                "User",
                "admin@musicshop.com",
                "0987654321",
                "admin123",
                UserRole.ADMIN,
                "Admin Avenue",
                "1",
                "10100",
                "Rotterdam",
                "Netherlands");

        upsertSeedUser(
                "Jane",
                "Smith",
                "jane.smith@example.com",
                "5551234567",
                "password123",
                UserRole.CUSTOMER,
                "River Road",
                "15B",
                "20002",
                "Utrecht",
                "Netherlands");
    }

    public User getDefaultUser() {
        return userRepository.findAll().stream().findFirst().orElse(null);
    }

    private void upsertSeedUser(
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String rawPassword,
            UserRole role,
            String street,
            String number,
            String postalCode,
            String city,
            String country) {
        User user = userRepository.findByEmail(email).orElseGet(User::new);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        User savedUser = userRepository.save(user);

        Cart cart = cartRepository.findByUser(savedUser).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(savedUser);
            newCart.setDateCreated(LocalDateTime.now());
            return cartRepository.save(newCart);
        });
        cartDetailRepository.deleteAll(cartDetailRepository.findByCart(cart));

        upsertAddress(savedUser, street, number, postalCode, city, country);
    }

    private void upsertAddress(User user, String street, String number, String postalCode, String city, String country) {
        UserAddress relation = userAddressRepository.findFirstByUserId(user.getId()).orElse(null);
        Address address = relation != null ? relation.getAddress() : new Address();
        address.setStreet(street);
        address.setNumber(number);
        address.setPostalCode(postalCode);
        address.setCity(city);
        address.setCountry(country);
        Address savedAddress = addressRepository.save(address);

        if (relation == null) {
            UserAddress newRelation = new UserAddress();
            newRelation.setUser(user);
            newRelation.setAddress(savedAddress);
            userAddressRepository.save(newRelation);
        }
    }
}
