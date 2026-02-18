package com.musicshop.controller.user;

import com.musicshop.application.user.UserUseCase;
import com.musicshop.dto.user.UpdateUserRequest;
import com.musicshop.dto.user.UserDTO;
import com.musicshop.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserUseCase userUseCase;

    @Autowired
    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @GetMapping("/{userId}")
    @PreAuthorize("@accessGuard.canAccessUser(#userId, authentication)")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
        return userUseCase.getUser(userId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("@accessGuard.canAccessUser(#userId, authentication)")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @Valid @RequestBody UpdateUserRequest request) {
        UserDTO updatedUser = userUseCase.updateUser(userId, request);
        return ResponseEntity.ok(updatedUser);
    }
}
