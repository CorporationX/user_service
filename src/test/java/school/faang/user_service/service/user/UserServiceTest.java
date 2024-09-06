package school.faang.user_service.service.user;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    private final Long LONG_POSITIVE_USER_ID_IS_ONE = 1L;

    @Nested
    class PositiveTest {
        @Test
        @DisplayName("Успех если пользователь существует")
        public void When_UserExist_Then_Success() {
            Goal goal = new Goal();
            goal.setId(LONG_POSITIVE_USER_ID_IS_ONE);

            User user = new User();
            user.setId(LONG_POSITIVE_USER_ID_IS_ONE);

            when(userRepository.findByIdWithGoals(LONG_POSITIVE_USER_ID_IS_ONE)).thenReturn(Optional.of(user));

            userService.addGoalToUserGoals(LONG_POSITIVE_USER_ID_IS_ONE, goal);

            verify(userRepository, times(1)).save(user);
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Ошибка валидации если пользователя не существует")
        public void When_UserNotExist_Then_ThrowValidationException() {
            Goal goal = new Goal();
            goal.setId(LONG_POSITIVE_USER_ID_IS_ONE);

            when(userRepository.findByIdWithGoals(LONG_POSITIVE_USER_ID_IS_ONE)).thenReturn(Optional.empty());

            assertThrows(ValidationException.class,
                    () -> userService.addGoalToUserGoals(LONG_POSITIVE_USER_ID_IS_ONE, goal),
                    "User with id " + LONG_POSITIVE_USER_ID_IS_ONE + " not exist");
        }
    }
}