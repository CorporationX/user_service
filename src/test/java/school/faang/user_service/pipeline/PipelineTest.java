package school.faang.user_service.pipeline;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class PipelineTest {

    @Test
    void intentionalFailure() {
        // This test will always fail
        fail("This test is meant to fail explicitly!");
    }
}
