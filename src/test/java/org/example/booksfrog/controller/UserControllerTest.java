package org.example.booksfrog.controller;

import org.example.booksfrog.model.User;
import org.example.booksfrog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

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
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user.getId(), response.getBody().getId());

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testCreateUser() {
        when(userService.createUser(any(User.class))).thenReturn(user);

        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user.getUsername(), response.getBody().getUsername());

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void testUpdateUser_UserExists() {
        User updatedUser = User.builder()
                .id(1L)
                .username("updateduser")
                .email("updated@example.com")
                .password("newpassword")
                .isPremium(false)
                .build();

        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(userService.updateUser(any(User.class))).thenReturn(updatedUser);

        ResponseEntity<User> response = userController.updateUser(1L, updatedUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedUser.getUsername(), response.getBody().getUsername());

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).updateUser(any(User.class));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.updateUser(1L, user);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(userService, times(1)).getUserById(1L);
        verify(userService, never()).updateUser(any(User.class));
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(userService, times(1)).deleteUser(1L);
    }
}
