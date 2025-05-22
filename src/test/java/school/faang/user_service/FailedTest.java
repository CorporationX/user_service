package school.faang.user_service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class FailedTest {
    @Test
    public void testThatShouldFail() {
        fail("Этот тест должен упасть");
    }
    @Test
    public void secondTestThatShouldFail() {
        fail("Этот тест должен упасть");
    }

    public void testThatAlwaysPasses() {
        assertTrue(true, "Этот тест всегда должен проходить");
    }
}