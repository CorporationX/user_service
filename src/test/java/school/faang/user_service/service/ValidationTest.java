package school.faang.user_service.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationTest {

    @Test
    public void shouldBeSuccess(){
        String t1 = "aa";
        String t2 = "aa";
        assertEquals(t1, t2);
    }

    @Test
    public void shouldBeFailedTest() {
        assertEquals(2, 3);
    }

}