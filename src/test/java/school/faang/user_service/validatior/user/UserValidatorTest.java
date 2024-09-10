package school.faang.user_service.validatior.user;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @InjectMocks
    private UserValidator userValidator;

    @Mock
    private UserRepository userRepository;

    private final long USER_ID_IS_NEGATIVE_ONE = -1L;
    private final long USER_ID_IS_ONE = 1L;

    @Nested
    class NegativeTests {

        @Nested
        class UserIdIsPositiveAndNotNullOrElseThrowValidationExceptionMethod {
            @Test
            @DisplayName("Ошибка валидации если id пользователя null")
            void whenNullValueThenThrowValidationException() {
                assertThrows(ValidationException.class,
                        () -> userValidator.userIdIsPositiveAndNotNullOrElseThrowValidationException(null),
                        "User id can't be null");
            }

            @Test
            @DisplayName("Ошибка валидации если id пользователя отрицательное")
            void whenNegativeValueThenThrowValidationException() {
                assertThrows(ValidationException.class,
                        () -> userValidator
                                .userIdIsPositiveAndNotNullOrElseThrowValidationException(USER_ID_IS_NEGATIVE_ONE),
                        "User id can't be less than 0");
            }
        }

        @Nested
        class UserIsExistedOrElseThrowValidationExceptionMethod {
            @Test
            @DisplayName("Ошибка валидации если пользователя с переданным id не существует")
            void whenUserNotExistsThenThrowValidationException() {
                when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

                assertThrows(ValidationException.class,
                        () -> userValidator.userIsExistedOrElseThrowValidationException(USER_ID_IS_ONE),
                        "User with id " + USER_ID_IS_ONE + " not exists");
            }
        }
    }

    @Nested
    class PositiveTests {

        @Nested
        class UserIdIsPositiveAndNotNullOrElseThrowValidationExceptionMethod {
            @Test
            @DisplayName("Если переданный id пользователя положительный и не null, то метод ничего не делает")
            void whenUserIdNotNullAndPositiveValueThenSuccess() {

                userValidator.userIdIsPositiveAndNotNullOrElseThrowValidationException(USER_ID_IS_ONE);
            }
        }

        @Nested
        class UserIsExistedOrElseThrowValidationExceptionMethod {
            @Test
            @DisplayName("Если пользователь с переданным id существует, то метод ничего не делает")
            void whenUserExistsThenSuccess() {
                when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

                userValidator.userIsExistedOrElseThrowValidationException(USER_ID_IS_ONE);

                verify(userRepository).findById(USER_ID_IS_ONE);
            }
        }
    }
}