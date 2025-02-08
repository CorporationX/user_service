package school.faang.user_service.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationTest {

    @Test
    void shouldBeSuccess() {
        String t1 = "hh";
        String k2 = "hh";
        assertEquals(t1, k2);
    }
}