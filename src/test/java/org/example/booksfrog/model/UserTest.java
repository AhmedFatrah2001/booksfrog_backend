package org.example.booksfrog.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserBuilder() {
        User user = User.builder()
                .username("testUser")
                .email("test@example.com")
                .password("password123")
                .isPremium(true)
                .build();

        assertNotNull(user);
        assertEquals("testUser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertTrue(user.isPremium());
    }

    @Test
    void testOnCreateSetsRegistrationDate() {
        User user = new User();
        user.onCreate(); // Simulate the lifecycle hook

        assertNotNull(user.getRegistrationDate());
        assertTrue(user.getRegistrationDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testIsPremium() {
        User premiumUser = User.builder().isPremium(true).build();
        User nonPremiumUser = User.builder().isPremium(false).build();

        assertTrue(premiumUser.isPremium());
        assertFalse(nonPremiumUser.isPremium());
    }

    @Test
    void testSettersAndGetters() {
        User user = new User();
        user.setUsername("newUser");
        user.setEmail("new@example.com");
        user.setPassword("newPassword");
        user.setProfilePicture(new byte[]{1, 2, 3});
        user.setIsPremium(false);

        assertEquals("newUser", user.getUsername());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("newPassword", user.getPassword());
        assertArrayEquals(new byte[]{1, 2, 3}, user.getProfilePicture());
        assertFalse(user.isPremium());
    }
}
