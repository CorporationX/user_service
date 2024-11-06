package school.faang.user_service.service.fail_test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FailTest {
    @Test
    void testFail() {
        Assertions.fail("fail test");
    }
}
