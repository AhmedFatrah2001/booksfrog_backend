package org.example.booksfrog.controller;

import org.example.booksfrog.model.User;
import org.example.booksfrog.service.CustomUserDetailsService;
import org.example.booksfrog.service.UserService;
import org.example.booksfrog.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

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
    void testLogin_Success() {
        AuthRequest authRequest = new AuthRequest("testuser", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(mock(UserDetails.class));
        when(jwtUtil.generateToken("testuser")).thenReturn("test-token");
        when(userService.findByUsername("testuser")).thenReturn(user);

        ResponseEntity<?> response = authController.login(authRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertNotNull(authResponse);
        assertEquals("test-token", authResponse.getToken());
        assertEquals("testuser", authResponse.getUsername());
    }

    @Test
    void testLogin_UserNotFound() {
        AuthRequest authRequest = new AuthRequest("testuser", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(mock(UserDetails.class));
        when(jwtUtil.generateToken("testuser")).thenReturn("test-token");
        when(userService.findByUsername("testuser")).thenReturn(null);

        ResponseEntity<?> response = authController.login(authRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found.", response.getBody());
    }

    @Test
    void testRegister_Success() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");
        registerRequest.setIsPremium(true);

        when(userService.isUsernameTaken("testuser")).thenReturn(false);
        when(userService.isEmailTaken("test@example.com")).thenReturn(false);
        when(userService.createUser(any(User.class))).thenReturn(user);

        ResponseEntity<?> response = authController.register(registerRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        User createdUser = (User) response.getBody();
        assertNotNull(createdUser);
        assertEquals("testuser", createdUser.getUsername());
    }

    @Test
    void testRegister_UsernameOrEmailTaken() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");

        when(userService.isUsernameTaken("testuser")).thenReturn(true);

        ResponseEntity<?> response = authController.register(registerRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username or email already taken.", response.getBody());
    }

    @Test
    void testRegister_InvalidRequest() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("");
        registerRequest.setEmail(null);
        registerRequest.setPassword("");

        ResponseEntity<?> response = authController.register(registerRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username, email, and password are mandatory.", response.getBody());
    }
}
