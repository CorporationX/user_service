package school.faang.user_service.validator.user;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
class UserValidatorTest {

    @InjectMocks
    private UserValidator userValidator;
    @Mock
    private UserRepository userRepository;

    private final long LONG_NEGATIVE_USER_ID_IS_ONE = -1L;
    private final long LONG_POSITIVE_USER_ID_IS_ONE = 1L;

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Ошибка валидации если переданное число null")
        void When_NullValue_Then_ThrowValidationException() {
            assertThrows(ValidationException.class,
                    () -> userValidator.userIdIsPositiveAndNotNullOrElseThrowValidationException(null),
                    "User id can't be null");
        }

        @Test
        @DisplayName("Ошибка валидации если переданное число отрицательное")
        void When_NegativeValue_Then_ThrowValidationException() {
            assertThrows(ValidationException.class,
                    () -> userValidator
                            .userIdIsPositiveAndNotNullOrElseThrowValidationException(LONG_NEGATIVE_USER_ID_IS_ONE),
                    "User id can't be less than 0");
        }

        @Test
        @DisplayName("Ошибка валидации если пользователя с переданным id не существует")
        void When_UserNotExists_Then_ThrowValidationException() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(ValidationException.class,
                    () -> userValidator.userIsExistedOrElseThrowValidationException(LONG_POSITIVE_USER_ID_IS_ONE),
                    "User with id " + LONG_POSITIVE_USER_ID_IS_ONE + " not exists");
        }
    }
}