package school.faang.user_service.service.validator;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.UserValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.user.UserValidator;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new UserValidator(userRepository);
    }

    @Test
    public void testValidateUserId() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(false);
        Exception exceptionForNull = Assert.assertThrows(UserValidationException.class, () -> validator.validateUserId(null));
        Exception exceptionForNegativeId = Assert.assertThrows(UserValidationException.class, () -> validator.validateUserId(-1L));
        Exception exceptionForNotFoundUser = Assert.assertThrows(UserValidationException.class, () -> validator.validateThatUserIdExist(1L));
        String expectedMessage = "user id is either null or less than zero";
        String expectedMessageForNotFoundUser = "user wasn't found";
        Assertions.assertEquals(expectedMessage, exceptionForNull.getMessage());
        Assertions.assertEquals(expectedMessage, exceptionForNegativeId.getMessage());
        Assertions.assertEquals(expectedMessageForNotFoundUser, exceptionForNotFoundUser.getMessage());
    }
}
