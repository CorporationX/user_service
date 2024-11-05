package school.faang.user_service.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class FailTest {

    @Test
    void thisTestShouldFail(){
        fail("This test is designed to fail for pipeline testing purposes.");
    }
}
