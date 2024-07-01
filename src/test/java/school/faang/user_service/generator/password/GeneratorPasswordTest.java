package school.faang.user_service.generator.password;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class GeneratorPasswordTest {
    private final UserPasswordGenerator generator = new UserPasswordGenerator();

    @Test
    public void testCreatePassword() {
        String password = generator.createPassword();
        assertNotNull(password);
        assertTrue(password.length() >= 8 && password.length() <= 12);
        assertTrue(password.matches(".*[a-z].*"));
        assertTrue(password.matches(".*[A-Z].*"));
        assertTrue(password.matches(".*[0-9].*"));
        assertTrue(password.matches(".*[!@#$%&*()+?].*"));
    }
}
