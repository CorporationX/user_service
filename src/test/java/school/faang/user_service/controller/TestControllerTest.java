package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestControllerTest {

    @Test
    void shouldFailTest() {
        // Намеренно проваленный тест
        String expected = "This should fail";
        String actual = "This is a test endpoint.";
        assertEquals(expected, actual, "The test is designed to fail.");
    }

}
