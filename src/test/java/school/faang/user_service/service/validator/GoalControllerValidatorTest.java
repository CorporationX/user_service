package school.faang.user_service.service.validator;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import school.faang.user_service.validator.GoalControllerValidate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class GoalControllerValidatorTest {
    @InjectMocks
    GoalControllerValidate validator;

    @Test
    public void testValidateId() {
        long id = 0;
        assertThrows(IllegalArgumentException.class, () -> validator.validateId(id));
    }

    @Test
    public void testValidateIdWhenValid() {
        long id = 1;
        assertDoesNotThrow(() -> validator.validateId(id));
    }
}
