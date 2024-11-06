package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestFail {
    @Test
    public void testThatFails() {
        Assertions.fail("Тест который всегда падает");
    }
}
