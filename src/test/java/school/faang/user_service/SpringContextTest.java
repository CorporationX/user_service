package school.faang.user_service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = UserServiceApplication.class)
@Testcontainers
public class SpringContextTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Spring context test check")
    public void contextLoads() {
        assertNotNull( applicationContext );
    }
}
