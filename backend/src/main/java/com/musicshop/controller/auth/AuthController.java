package com.musicshop.controller.auth;

import com.musicshop.dto.auth.AuthResponse;
import com.musicshop.dto.auth.LoginRequest;
import com.musicshop.dto.auth.RegisterRequest;
import com.musicshop.model.cart.Cart;
import com.musicshop.model.user.User;
import com.musicshop.model.user.UserRole;
import com.musicshop.repository.cart.CartRepository;
import com.musicshop.repository.user.UserRepository;
import com.musicshop.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository,
            CartRepository cartRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already in use");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        userRepository.save(user);

        // Create a cart for the new user
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setDateCreated(LocalDateTime.now());
        cartRepository.save(cart);

        String token = jwtService.generateToken(user);
        AuthResponse response = new AuthResponse(
                token, user.getId(), user.getEmail(), user.getFirstName(), user.getRole().name());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        AuthResponse response = new AuthResponse(
                token, user.getId(), user.getEmail(), user.getFirstName(), user.getRole().name());
        return ResponseEntity.ok(response);
    }
}
