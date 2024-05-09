import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class GithubCiPipelineTest {

    @Test
    public void testWhichMustFail() {
        fail("I should fail!");
    }
}
