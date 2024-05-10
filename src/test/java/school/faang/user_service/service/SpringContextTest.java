package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import school.faang.user_service.UserServiceApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class SpringContextTest {

    @Test
    void testSpringContextInitialization(ApplicationContext applicationContext) {
        assertNotNull(applicationContext);
        assertNotNull(applicationContext.getBean(UserServiceApplication.class));
    }
}
