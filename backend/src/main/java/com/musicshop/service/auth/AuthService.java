package com.musicshop.service.auth;

import com.musicshop.dto.auth.AuthResponse;
import com.musicshop.dto.auth.LoginRequest;
import com.musicshop.dto.auth.RegisterRequest;
import com.musicshop.exception.DuplicateResourceException;
import com.musicshop.exception.ResourceNotFoundException;
import com.musicshop.mapper.AuthMapper;
import com.musicshop.model.cart.Cart;
import com.musicshop.model.user.User;
import com.musicshop.model.user.UserRole;
import com.musicshop.repository.cart.CartRepository;
import com.musicshop.repository.user.UserRepository;
import com.musicshop.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthMapper authMapper;

    @Autowired
    public AuthService(UserRepository userRepository,
                       CartRepository cartRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthMapper authMapper) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authMapper = authMapper;
    }

    @Transactional
    public AuthResult register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already in use");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setDateCreated(LocalDateTime.now());
        cartRepository.save(cart);

        String token = jwtService.generateToken(user);
        AuthResponse response = toAuthResponse(user, token);

        return new AuthResult(response, token);
    }

    public AuthResult login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        AuthResponse response = toAuthResponse(user, token);

        return new AuthResult(response, token);
    }

    public AuthResponse getAuthenticatedUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toAuthResponse(user, null);
    }

    private AuthResponse toAuthResponse(User user, String token) {
        return authMapper.toAuthResponse(user, token);
    }

    public static class AuthResult {
        private final AuthResponse response;
        private final String token;

        public AuthResult(AuthResponse response, String token) {
            this.response = response;
            this.token = token;
        }

        public AuthResponse getResponse() {
            return response;
        }

        public String getToken() {
            return token;
        }
    }
}
