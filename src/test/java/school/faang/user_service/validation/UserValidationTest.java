package school.faang.user_service.validation;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidationTest {

    @InjectMocks
    private UserValidation userValidation;

    @Mock
    private UserRepository userRepository;

    private final long NEGATIVE_VALUE = -1;
    private final long POSITIVE_VALUE = 1;

    @Test
    void testValidateUserWithNegativeId() {
        long userId = NEGATIVE_VALUE;

        assertThrows(ValidationException.class,
                () -> userValidation.validateUserId(userId),
                "User id can't be less than 0");
    }

    @Test
    void testValidateUserWithNotExistedUser() {
        long userId = POSITIVE_VALUE;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ValidationException.class,
                () -> userValidation.validateUserExists(userId),
                "User with id " + userId + " not exists");
    }

}