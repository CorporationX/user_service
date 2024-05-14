package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.repository.SkillRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

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
    private SkillRepository skillRepository;

    @InjectMocks
    private GoalValidator validator;

    @Test
    void testValidateGoalId_throwsDataValidationExceptionIfIdIsSmaller() {
        GoalValidator validator = new GoalValidator(null);
        assertThrows(DataValidationException.class, () -> validator.validateGoalId(0));
    }

    @Test
    void testValidateThatIdIsGreaterThan0_withValidId_doesNotThrowException() {
        GoalValidator validator = new GoalValidator(null);
        assertDoesNotThrow(() -> validator.validateGoalId(1));
    }

    @Test
    void testValidateUserGoalsAmount_ThrowsExceptionIfUserReachedGoalsAmountLimit() {
        int MAX_GOALS_AMOUNT = 3;
        when(user.getGoals()).thenReturn(Collections.nCopies(MAX_GOALS_AMOUNT, new Goal()));
        assertThrows(DataValidationException.class, () -> validator.validateUserGoalsAmount(user));
    }

    @Test
    public void validateAllSkillsExist_someSkillsDoNotExist_notFoundExceptionThrown() {

        GoalDto goalDto = new GoalDto();
        goalDto.setSkillIds(Arrays.asList(1L, 2L, 3L));

        when(skillRepository.findById(1L)).thenReturn(Optional.of(new Skill()));
        when(skillRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> validator.validateAllSkillsExist(goalDto));
    }

    @Test
    public void testValidateGoalUpdate_ThrowsExceptionIfGoalCompleted() {
        GoalStatus goalStatus = GoalStatus.COMPLETED;
        when(goal.getStatus()).thenReturn(goalStatus);

        assertThrows(DataValidationException.class, () -> validator.validateGoalNotCompleted(goal));

        verify(goal, times(1)).getStatus();
    }
}