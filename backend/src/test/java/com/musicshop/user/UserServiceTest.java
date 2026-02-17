package com.musicshop.user;

import com.musicshop.dto.user.UpdateUserRequest;
import com.musicshop.dto.user.UserDTO;
import com.musicshop.exception.ResourceNotFoundException;
import com.musicshop.model.user.User;
import com.musicshop.repository.user.UserRepository;
import com.musicshop.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void getUser_whenUserExists() {
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmail("john@example.com");
        mockUser.setPhoneNumber("123456789");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        Optional<UserDTO> result = userService.getUser(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        assertEquals("John", result.get().getFirstName());
        verify(userRepository).findById(userId);
    }

    @Test
    void getUser_whenUserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<UserDTO> result = userService.getUser(userId);

        assertFalse(result.isPresent());
        verify(userRepository).findById(userId);
    }

    @Test
    void updateUser_whenUserExists() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setFirstName("Original");
        existingUser.setLastName("User");
        existingUser.setEmail("original@example.com");
        existingUser.setPhoneNumber("000000000");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Updated");
        request.setLastName("User");
        request.setEmail("updated@example.com");
        request.setPhoneNumber("111111111");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO updatedUser = userService.updateUser(userId, request);

        assertEquals("Updated", updatedUser.getFirstName());
        assertEquals("updated@example.com", updatedUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_whenUserDoesNotExist() {
        Long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Updated");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(userId, request));
        verify(userRepository, never()).save(any(User.class));
    }
}
