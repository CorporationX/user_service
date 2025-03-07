package school.faang.user_service.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class FailTest {
    @Test
    public void checkFailTest() {
        fail();
    }

    @Test
    public void checkAsserEqTest() {
        assertEquals(1, 2);
    }

    @Test
    public void checkStyleTest() {
        assertEquals("faang_faang_faang_faang_faang_faang_faang_faang_faang_faang", "faang_faang_faang_faang_faang_faang_faang_faang_faang_faang");
    }
}
