package school.faang.user_service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpringContextTest {

    @Test
    @DisplayName("Spring context test check")
    void shouldLoadSpringContext() {
        System.out.println("Spring boot context is loaded");
    }
}
