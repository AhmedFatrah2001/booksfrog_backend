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

import java.util.Collections;

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

    private AuthRequest authRequest;
    private RegisterRequest registerRequest;
    private User user;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        authRequest = new AuthRequest("testuser", "password");
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password");

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("testuser@example.com")
                .password("password")
                .isPremium(false)
                .build();

        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    void testAuthRequestConstructor() {
        AuthRequest request = new AuthRequest("username", "password");
        assertEquals("username", request.getUsername());
        assertEquals("password", request.getPassword());
    }

    @Test
    void testRegisterRequestConstructorAndSetters() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("username");
        request.setEmail("email@example.com");
        request.setPassword("password");
        request.setIsPremium(true);
        request.setProfilePicture(new byte[]{1, 2, 3});

        assertEquals("username", request.getUsername());
        assertEquals("email@example.com", request.getEmail());
        assertEquals("password", request.getPassword());
        assertTrue(request.getIsPremium());
        assertArrayEquals(new byte[]{1, 2, 3}, request.getProfilePicture());
    }

//    @Test
//    void testAuthResponseConstructor() {
//        AuthResponse response = new AuthResponse("jwt-token", user);
//
//        assertEquals("jwt-token", response.getToken());
//        assertEquals(user.getUsername(), response.getUsername());
//        assertEquals(user.getEmail(), response.getEmail());
//        assertEquals(user.isPremium(), response.isPremium());
//        assertNull(response.getProfilePicture());
//    }

    @Test
    void testLogin_Success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userDetailsService.loadUserByUsername(authRequest.getUsername())).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails.getUsername())).thenReturn("jwt-token");
        when(userService.findByUsername(authRequest.getUsername())).thenReturn(user);

        ResponseEntity<?> response = authController.login(authRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertNotNull(authResponse);
        assertEquals("jwt-token", authResponse.getToken());
        assertEquals(user.getUsername(), authResponse.getUsername());
    }

    @Test
    void testLogin_UserNotFound() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userDetailsService.loadUserByUsername(authRequest.getUsername())).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails.getUsername())).thenReturn("jwt-token");
        when(userService.findByUsername(authRequest.getUsername())).thenReturn(null);

        ResponseEntity<?> response = authController.login(authRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found.", response.getBody());
    }

    @Test
    void testRegister_Success() {
        when(userService.isUsernameTaken(registerRequest.getUsername())).thenReturn(false);
        when(userService.isEmailTaken(registerRequest.getEmail())).thenReturn(false);
        when(userService.createUser(any(User.class))).thenReturn(user);

        ResponseEntity<?> response = authController.register(registerRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        User createdUser = (User) response.getBody();
        assertNotNull(createdUser);
        assertEquals(user.getUsername(), createdUser.getUsername());
    }

    @Test
    void testRegister_UsernameOrEmailTaken() {
        when(userService.isUsernameTaken(registerRequest.getUsername())).thenReturn(true);

        ResponseEntity<?> response = authController.register(registerRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username or email already taken.", response.getBody());
    }

    @Test
    void testRegister_MissingMandatoryFields() {
        registerRequest.setUsername("");

        ResponseEntity<?> response = authController.register(registerRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username, email, and password are mandatory.", response.getBody());
    }
}
