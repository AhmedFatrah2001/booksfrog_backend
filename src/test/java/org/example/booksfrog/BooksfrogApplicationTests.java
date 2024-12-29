package org.example.booksfrog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(classes = BooksfrogApplication.class)
@ActiveProfiles("test")
class BooksfrogApplicationTests {

    @Test
    void contextLoads() {
        // This test ensures that the Spring application context loads successfully.
        // No additional implementation is required.
    }
    @Test
    void testMain() {
        // Ensures the main method runs without throwing any exceptions
        assertDoesNotThrow(() -> BooksfrogApplication.main(new String[]{}));
    }

}
