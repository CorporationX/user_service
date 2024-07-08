package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestPipeline {

    @Test
    public void testFailed() {
        String expected = "expected";
        Assertions.assertEquals(expected, "fail");

    }
}
