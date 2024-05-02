package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.TooManyGoalsException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalValidatorTest {

    @Mock
    private User user;
    @Mock
    private Goal goal;
    @Mock
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private GoalValidator validator;
    private final int MAX_GOALS_AMOUNT = 5;

    @Test
    void shouldThrowTooManyGoalsException() {
        when(user.getGoals()).thenReturn(Collections.nCopies(MAX_GOALS_AMOUNT, new Goal()));
        assertThrows(TooManyGoalsException.class, () -> validator.validateGoalCreation(user, goal, MAX_GOALS_AMOUNT));
    }

    @Test
    public void testValidateGoalUpdate_ThrowsExceptionIfGoalCompleted() {
        GoalStatus goalStatus = GoalStatus.COMPLETED;
        when(goal.getStatus()).thenReturn(goalStatus);

        DataValidationException thrown = assertThrows(
                DataValidationException.class,
                () -> validator.validateGoalUpdate(goal)
        );

        verify(goal, times(1)).getStatus();
    }

    @Test
    void testValidateThatIdIsGreaterThan0_withInvalidId_throwsDataValidationException() {
        GoalValidator validator = new GoalValidator(null);
        assertThrows(DataValidationException.class, () -> validator.validateThatIdIsGreaterThan0(0));
    }

    @Test
    void testValidateThatIdIsGreaterThan0_withValidId_doesNotThrowException() {
        GoalValidator validator = new GoalValidator(null);
        assertDoesNotThrow(() -> validator.validateThatIdIsGreaterThan0(1));
    }

    @Test
    void testValidateFindSubtasks_withNoSubtasks_throwsEntityNotFoundException() {
        List<Goal> subtasks = Collections.emptyList();
        long goalId = 1L;
        GoalValidator validator = new GoalValidator(null);
        assertThrows(EntityNotFoundException.class, () -> validator.validateFindSubtasks(subtasks, goalId));
    }
}