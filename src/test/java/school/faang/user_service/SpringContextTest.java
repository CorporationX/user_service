package school.faang.user_service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
public class SpringContextTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void springContextLoadSuccess() {
        Assertions.assertNotNull(applicationContext);
    }
}