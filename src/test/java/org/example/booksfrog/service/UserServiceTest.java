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

    private BCryptPasswordEncoder passwordEncoder;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("testUser");
        sampleUser.setEmail("test@example.com");
        sampleUser.setPassword("password123");
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        Optional<User> user = userService.getUserById(1L);

        assertTrue(user.isPresent());
        assertEquals("testUser", user.get().getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserByUsername() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(sampleUser));

        Optional<User> user = userService.getUserByUsername("testUser");

        assertTrue(user.isPresent());
        assertEquals("test@example.com", user.get().getEmail());
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testGetUserByEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(sampleUser));

        Optional<User> user = userService.getUserByEmail("test@example.com");

        assertTrue(user.isPresent());
        assertEquals("testUser", user.get().getUsername());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }



    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }



    @Test
    void testCheckPassword() {
        String rawPassword = "password123";
        String hashedPassword = passwordEncoder.encode(rawPassword);

        assertTrue(userService.checkPassword(rawPassword, hashedPassword));
        assertFalse(userService.checkPassword("wrongPassword", hashedPassword));
    }

    @Test
    void testIsUsernameTaken() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(sampleUser));

        assertTrue(userService.isUsernameTaken("testUser"));
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testIsEmailTaken() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(sampleUser));

        assertTrue(userService.isEmailTaken("test@example.com"));
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }
}
