package school.faang.user_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import school.faang.user_service.config.TestRedisConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestRedisConfiguration.class)
class UserServiceApplicationTest {

    @Test
    void contextLoads() {}
}