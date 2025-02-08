package school.faang.user_service.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ValidationTest {

    @Test
    public void shouldBeSuccess() {
        String t1 = "hh";
        String k2 = "hh";
        assertEquals(t1, k2);
    }

    @Test
    public void checkstyleShoulfail() {
        assertFalse(false);
    }

    @Test
    public void shouldBeFailedTest() {
        assertEquals(2, 3);
    }
}