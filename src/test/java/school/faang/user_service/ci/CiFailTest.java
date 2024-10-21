package school.faang.user_service.ci;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

class CiFailTest {

    @Test
    @DisplayName("failed test for ci check")
    void ciTest_checkFailedTest() {
        fail();
    }

    @Test
    @DisplayName("test")
    void ciTest_test() {
    }
}
