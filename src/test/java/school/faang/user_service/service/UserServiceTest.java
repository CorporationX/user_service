package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @Test
    public void testSimpleLogic() {
        assertEquals(3, 1+2);
    }
    @Test
    public void testException() {
        fail();
    }
}