package org.example.booksfrog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = BooksfrogApplication.class)
@ActiveProfiles("test")
class BooksfrogApplicationTests {

    @Test
    void contextLoads() {
        // This test ensures that the Spring application context loads successfully.
        // No additional implementation is required.
    }

}
