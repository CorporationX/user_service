package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OnlyForTestsTest {
    private OnlyForTests onlyForTests = new OnlyForTests();

    @BeforeEach
    public void setUp() {
        onlyForTests = new OnlyForTests();
    }

    @Test
    public void doSomethingTest_Test1() {
        String input = "test1";

        String result = onlyForTests.doSomething(input);

        assertEquals(input, result);
    }

    @Test
    public void doSomethingTest_Test2() {
        String input = "test2";

        String result = onlyForTests.doSomething(input);

        assertEquals(input, result);
    }

    @Test
    public void doSomethingTest_Test3() {
        String input = "test3";

        String result = onlyForTests.doSomething(input);

        assertEquals(input, result);
    }

    @Test
    public void doSomethingTest_Unknown() {
        String input = "unknown";

        String result = onlyForTests.doSomething(input);

        assertEquals(input, result);
    }
}