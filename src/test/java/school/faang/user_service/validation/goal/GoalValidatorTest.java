package school.faang.user_service.validation.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalValidatorTest {

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private GoalRepository goalRepository;
    @InjectMocks
    private GoalValidator goalValidator;

    @Test
    void validateGoalCreation_MaxGoals_ThrowsException() {
        Long userId = 1L;
        when(goalRepository.countActiveGoalsPerUser(anyLong())).thenReturn(goalValidator.MAX_USER_ACTIVE_GOALS);

        assertThrows(DataValidationException.class, ()
                -> goalValidator.validateGoalCreation(userId, getValidGoalDto()));
    }

    @Test
    void validateGoalCreation_SkillNotExist_ThrowsException() {
        Long userId = 1L;
        when(goalRepository.countActiveGoalsPerUser(anyLong())).thenReturn(goalValidator.MAX_USER_ACTIVE_GOALS - 1);
        when(skillRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, ()
                -> goalValidator.validateGoalCreation(userId, getValidGoalDto()));
    }

    @Test
    void validateGoalCreation_ValidParams_DoesNotThrowException() {
        Long userId = 1L;
        int maxCountActiveGoalsPerUser = 3;
        when(goalRepository.countActiveGoalsPerUser(anyLong())).thenReturn(maxCountActiveGoalsPerUser - 1);
        when(skillRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(()
                -> goalValidator.validateGoalCreation(userId, getValidGoalDto()));
        verify(goalRepository, times(1)).countActiveGoalsPerUser(anyLong());
        verify(skillRepository, times(getSkillIds().size())).existsById(anyLong());
    }

    @Test
    void validateGoalUpdate_InvalidGoalId_ThrowsException() {
        Long goalId = 1L;
        when(goalRepository.existsById(goalId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, ()
                -> goalValidator.validateGoalUpdate(goalId, getValidGoalDto()));
    }

    @Test
    void validateGoalUpdate_InvalidStatus_ThrowsException() {
        Long goalId = 1L;
        when(goalRepository.existsById(goalId)).thenReturn(true);
        when(goalRepository.findById(goalId)).thenReturn(Optional.ofNullable(getGoalToUpdateInvalidStatus()));

        assertThrows(DataValidationException.class, ()
                -> goalValidator.validateGoalUpdate(goalId, getValidGoalDto()));
    }

    @Test
    void validateGoalUpdate_SkillNotExists_ThrowsException() {
        Long goalId = 1L;
        when(goalRepository.existsById(goalId)).thenReturn(true);
        when(goalRepository.findById(goalId)).thenReturn(Optional.ofNullable(getGoalToUpdateValidStatus()));
        when(skillRepository.existsById(goalId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, ()
                -> goalValidator.validateGoalUpdate(goalId, getValidGoalDto()));
    }

    @Test
    void validateGoalExists_NotExists_ThrowsException() {
        Long goalId = 1L;
        when(goalRepository.existsById(goalId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, ()
                -> goalValidator.validateGoalExists(goalId));
    }

    private GoalDto getValidGoalDto() {
        return GoalDto.builder()
                .title("title")
                .skillIds(getSkillIds())
                .build();
    }

    private List<Long> getSkillIds() {
        return List.of(1L, 2L);
    }

    private Goal getGoalToUpdateInvalidStatus() {
        return Goal.builder()
                .title("Title")
                .status(GoalStatus.COMPLETED)
                .build();
    }

    private Goal getGoalToUpdateValidStatus() {
        return Goal.builder()
                .title("Title")
                .status(GoalStatus.ACTIVE)
                .build();
    }
}