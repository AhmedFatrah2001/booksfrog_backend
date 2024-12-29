package org.example.booksfrog.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String token;
    private final String username = "testuser";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        token = jwtUtil.generateToken(username);
    }

    @Test
    void testGenerateToken() {
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testExtractUsername() {
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateToken_ValidToken() {
        assertTrue(jwtUtil.validateToken(token, username));
    }

    @Test
    void testValidateToken_InvalidUsername() {
        assertFalse(jwtUtil.validateToken(token, "wronguser"));
    }




    @Test
    void testIsTokenExpired_ExpiredToken() {
        SecretKey secretKey = jwtUtil.getSecretKey(); // Use the same key from JwtUtil
        String expiredToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 12)) // 12 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 10)) // Expired 10 hours ago
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        // Expect the validateToken to throw ExpiredJwtException
        assertThrows(ExpiredJwtException.class, () -> jwtUtil.validateToken(expiredToken, username));
    }



    @Test
    void testExtractClaim() {
        Claims claims = jwtUtil.extractClaim(token, Function.identity());
        assertEquals(username, claims.getSubject());
    }
}