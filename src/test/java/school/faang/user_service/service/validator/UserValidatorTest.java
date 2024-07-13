package school.faang.user_service.service.validator;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.UserValidationException;
import school.faang.user_service.validator.UserValidator;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    private UserValidator validator;

    @Test
    public void testValidateUserId() {
        Exception exceptionForNull = Assert.assertThrows(UserValidationException.class, () -> validator.validateUserId(null));
        Exception exceptionForNegativeId = Assert.assertThrows(UserValidationException.class, () -> validator.validateUserId(-1L));
        String expectedMessage = "user id is either null or less than zero";
        Assertions.assertEquals(expectedMessage, exceptionForNull.getMessage());
        Assertions.assertEquals(expectedMessage, exceptionForNegativeId.getMessage());
    }
}
