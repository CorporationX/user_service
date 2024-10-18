package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.model.dto.goal.GoalDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.goal.Goal;
import school.faang.user_service.model.enums.GoalStatus;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalValidatorTest {
    @InjectMocks
    private GoalValidator goalValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    long userId;
    int maxGoal;
    long goalId;
    GoalDto goalDto;

    @BeforeEach
    public void setUp() {
        userId = 1;
        maxGoal = 3;
        goalId = 1;
        goalDto = new GoalDto("Learn",
                "reading book",
                null,
                List.of(),
                null);
    }

    @Test
    @DisplayName("Проверка не существующего пользователя")
    public void givenNotValidWhenValidateUserIdThenException() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> goalValidator.validateUserId(userId));
    }

    @Test
    @DisplayName("Проверка на существующего пользователя")
    public void givenValidWhenValidateUserIdThenNotException() {
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> goalValidator.validateUserId(userId));
    }

    @Test
    @DisplayName("Проверка на максимальное количество активных целей у пользователя")
    public void givenNotValidWhenValidateCreationGoalThenException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(3);

        assertThrows(DataValidationException.class, () ->
                goalValidator.validateCreationGoal(userId));
    }

    @Test
    @DisplayName("Проверка на не существующий цели в БД")
    public void givenNotValidGoalIdWhenValidateUpdateThenException() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                goalValidator.validateUpdate(goalId));
    }

    @Test
    @DisplayName("Проверка, если статус цели = COMPLETED")
    public void givenStatusCompletedWhenValidateUpdateThenException() {
        Goal goal = new Goal();
        goal.setStatus(GoalStatus.COMPLETED);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        assertThrows(DataValidationException.class, () ->
                goalValidator.validateUpdate(userId));
    }

    @Test
    @DisplayName("Проверка на не существующей цели")
    public void givenNotValidWhenValidateGoalIdThenException() {
        assertThrows(ResourceNotFoundException.class, () ->
                goalValidator.validateGoalId(goalId));
    }
}
