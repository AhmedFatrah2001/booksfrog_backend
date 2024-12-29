package org.example.booksfrog.service;

import org.example.booksfrog.model.User;
import org.example.booksfrog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .isPremium(true)
                .build();
    }

    @Test
    void testGetUserById_UserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(1L);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals(user.getUsername(), createdUser.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUser(user);

        assertNotNull(updatedUser);
        assertEquals(user.getUsername(), updatedUser.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testIsUsernameTaken_UsernameExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        boolean isTaken = userService.isUsernameTaken("testuser");

        assertTrue(isTaken);
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testIsUsernameTaken_UsernameNotExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        boolean isTaken = userService.isUsernameTaken("testuser");

        assertFalse(isTaken);
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testCheckPassword_Match() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password";
        String encodedPassword = encoder.encode(rawPassword);

        assertTrue(userService.checkPassword(rawPassword, encodedPassword));
    }

    @Test
    void testCheckPassword_NoMatch() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password";
        String encodedPassword = encoder.encode("differentpassword");

        assertFalse(userService.checkPassword(rawPassword, encodedPassword));
    }
}