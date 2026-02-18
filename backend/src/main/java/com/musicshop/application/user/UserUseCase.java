package com.musicshop.application.user;

import com.musicshop.dto.user.UpdateUserRequest;
import com.musicshop.dto.user.UserDTO;
import com.musicshop.service.user.UserService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserUseCase {

    private final UserService userService;

    public UserUseCase(UserService userService) {
        this.userService = userService;
    }

    public Optional<UserDTO> getUser(Long userId) {
        return userService.getUser(userId);
    }

    public UserDTO updateUser(Long userId, UpdateUserRequest request) {
        return userService.updateUser(userId, request);
    }
}
