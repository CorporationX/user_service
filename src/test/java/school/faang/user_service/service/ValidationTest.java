package school.faang.user_service.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationTest {

    @Test
    public void shouldBeSuccess() {
        String t1 = "hh";
        String k2 = "hh";
        assertEquals(t1, k2);
    }

    @Test
    public void checkstyleShoulfail() {
        if (true){
            System.out.println("it's true");
        }
        if( 3 < 5 ) {
            System.out.println("no space after if");
        }
    }

    @Test
    public void shouldBeFailedTest() {
        assertEquals(2, 3);
    }
}