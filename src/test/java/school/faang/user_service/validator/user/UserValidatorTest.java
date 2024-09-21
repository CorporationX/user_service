package school.faang.user_service.validator.user;

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

    private static final long USER_ID_IS_NEGATIVE_ONE = -1L;
    private static final long USER_ID_IS_ONE = 1L;
    private static final long USER_ID_IS_TWO = 2L;

    @InjectMocks
    private UserValidator userValidator;

    @Mock
    private UserRepository userRepository;

    @Nested
    class NegativeTests {

        @Nested
        class UserIdIsPositiveAndNotNullOrElseThrowValidationExceptionMethod {

            @Test
            @DisplayName("Throws ValidationException when userId is null")
            void whenNullValueThenThrowValidationException() {
                assertThrows(ValidationException.class,
                        () -> userValidator.validateUserIdIsPositiveAndNotNull(null),
                        "User id can't be null");
            }

            @Test
            @DisplayName("Throws ValidationException when userId is negative")
            void whenNegativeValueThenThrowValidationException() {
                assertThrows(ValidationException.class,
                        () -> userValidator
                                .validateUserIdIsPositiveAndNotNull(USER_ID_IS_NEGATIVE_ONE),
                        "User id can't be less than 0");
            }
        }

        @Test
        @DisplayName("Throws ValidationException when user not exists in DB")
        void whenUserNotExistsThenThrowValidationException() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(ValidationException.class,
                    () -> userValidator.validateUserIsExisted(USER_ID_IS_ONE),
                    "User with id " + USER_ID_IS_ONE + " not exists");
        }

        @Test
        @DisplayName("Throws ValidationException when users id are equals")
        void whenId1AndId2EqualsThenThrowValidationExceptionWithMessage() {
            String exceptionMessage = "Exception";

            assertThrows(ValidationException.class,
                    () -> userValidator.validateFirstUserIdAndSecondUserIdNotEquals(
                            USER_ID_IS_ONE,
                            USER_ID_IS_ONE,
                            exceptionMessage),
                    exceptionMessage);
        }
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Success if userId is not null and positive")
        void whenUserIdNotNullValueAndMoreThanZeroThenSuccess() {
            userValidator.validateUserIdIsPositiveAndNotNull(USER_ID_IS_ONE);
        }

        @Test
        @DisplayName("Success if user is exists")
        void whenUserExistsThenSuccess() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

            userValidator.validateUserIsExisted(USER_ID_IS_ONE);

            verify(userRepository).findById(anyLong());
        }

        @Test
        @DisplayName("Success if users id aren't equals")
        void whenId1AndId2EqualsThenNotThrowValidationException() {
            String exceptionMessage = "Exception";

            userValidator.validateFirstUserIdAndSecondUserIdNotEquals(
                    USER_ID_IS_ONE,
                    USER_ID_IS_TWO,
                    exceptionMessage);
        }
    }
}