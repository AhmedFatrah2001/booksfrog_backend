package org.example.booksfrog.filter;

import org.example.booksfrog.service.CustomUserDetailsService;
import org.example.booksfrog.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        String token = "valid-token";
        String username = "testuser";
        request.addHeader("Authorization", "Bearer " + token);

        UserDetails userDetails = new User(username, "password", Collections.emptyList());

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, username)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        SecurityContext context = SecurityContextHolder.getContext();
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) context.getAuthentication();

        assertNotNull(authentication);
        assertEquals(username, authentication.getName());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        String token = "invalid-token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtUtil.extractUsername(token)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        SecurityContext context = SecurityContextHolder.getContext();
        assertNull(context.getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NoAuthorizationHeader() throws ServletException, IOException {
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        SecurityContext context = SecurityContextHolder.getContext();
        assertNull(context.getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

}
