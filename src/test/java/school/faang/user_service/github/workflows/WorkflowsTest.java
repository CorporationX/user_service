package school.faang.user_service.github.workflows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WorkflowsTest {

    @Test
    @DisplayName("Workflows failing test #1")
    void shouldBreak() {
        String foresight = "We will break the BigTech doors!";

        Assertions.assertEquals(foresight, false);
    }
}
