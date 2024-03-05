package school.faang.user_service.validation.goal;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.EntityFieldsException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.EntityUpdateException;
import school.faang.user_service.exception.goal.UserReachedMaxGoalsException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GoalValidatorTest {

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private GoalRepository goalRepository;
    @InjectMocks
    private GoalValidator goalValidator;

    @Test
    public void validateGoalCreationNullTitleThrowsException() {
        Long userId = 1L;
        int maxCountActiveGoalsPerUser = 3;
        assertThrows(EntityFieldsException.class, ()
                -> goalValidator.validateGoalCreation(userId, getGoalDtoNullTitle(), maxCountActiveGoalsPerUser));
    }

    @Test
    public void validateGoalCreationEmptyTitleThrowsException() {
        Long userId = 1L;
        int maxCountActiveGoalsPerUser = 3;
        assertThrows(EntityFieldsException.class, ()
                -> goalValidator.validateGoalCreation(userId, getGoalDtoEmptyTitle(), maxCountActiveGoalsPerUser));
    }

    @Test
    public void validateGoalCreationNullSkillsThrowsException() {
        Long userId = 1L;
        int maxCountActiveGoalsPerUser = 3;
        assertThrows(EntityFieldsException.class, ()
                -> goalValidator.validateGoalCreation(userId, getGoalDtoNullSkills(), maxCountActiveGoalsPerUser));
    }

    @Test
    public void validateGoalCreationEmptySkillsThrowsException() {
        Long userId = 1L;
        int maxCountActiveGoalsPerUser = 3;
        assertThrows(EntityFieldsException.class, ()
                -> goalValidator.validateGoalCreation(userId, getGoalDtoEmptySkills(), maxCountActiveGoalsPerUser));
    }

    @Test
    public void validateGoalCreationMaxGoalsThrowsException() {
        Long userId = 1L;
        int maxCountActiveGoalsPerUser = 3;
        when(goalRepository.countActiveGoalsPerUser(anyLong())).thenReturn(maxCountActiveGoalsPerUser);

        assertThrows(UserReachedMaxGoalsException.class, ()
                -> goalValidator.validateGoalCreation(userId, getValidGoalDto(), maxCountActiveGoalsPerUser));
    }

    @Test
    public void validateGoalCreationSkillNotExistThrowsException() {
        Long userId = 1L;
        int maxCountActiveGoalsPerUser = 3;
        when(goalRepository.countActiveGoalsPerUser(anyLong())).thenReturn(maxCountActiveGoalsPerUser - 1);
        when(skillRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, ()
                -> goalValidator.validateGoalCreation(userId, getValidGoalDto(), maxCountActiveGoalsPerUser));
    }

    @Test
    public void validateGoalCreationValidParams() {
        Long userId = 1L;
        int maxCountActiveGoalsPerUser = 3;
        when(goalRepository.countActiveGoalsPerUser(anyLong())).thenReturn(maxCountActiveGoalsPerUser - 1);
        when(skillRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(()
                -> goalValidator.validateGoalCreation(userId, getValidGoalDto(), maxCountActiveGoalsPerUser));
    }

    @Test
    public void validateGoalUpdateNullTitleThrowsException() {
        Long goalId = 1L;
        assertThrows(EntityFieldsException.class, ()
                -> goalValidator.validateGoalUpdate(goalId, getGoalDtoNullTitle()));
    }

    @Test
    public void validateGoalUpdateEmptyTitleThrowsException() {
        Long goalId = 1L;
        assertThrows(EntityFieldsException.class, ()
                -> goalValidator.validateGoalUpdate(goalId, getGoalDtoEmptyTitle()));
    }

    @Test
    public void validateGoalUpdateNotExistsThrowsException() {
        Long goalId = 1L;
        when(goalRepository.existsById(goalId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, ()
                -> goalValidator.validateGoalUpdate(goalId, getValidGoalDto()));
    }

    @Test
    public void validateGoalUpdateInvalidStatusThrowsException() {
        Long goalId = 1L;
        when(goalRepository.existsById(goalId)).thenReturn(true);
        when(goalRepository.findById(goalId)).thenReturn(Optional.ofNullable(getGoalToUpdateInvalidStatus()));

        assertThrows(EntityUpdateException.class, ()
                -> goalValidator.validateGoalUpdate(goalId, getValidGoalDto()));
    }

    @Test
    public void validateGoalUpdateNullSkillsThrowsException() {
        Long goalId = 1L;
        when(goalRepository.existsById(goalId)).thenReturn(true);
        when(goalRepository.findById(goalId)).thenReturn(Optional.ofNullable(getGoalToUpdateValidStatus()));

        assertThrows(EntityFieldsException.class, ()
                -> goalValidator.validateGoalUpdate(goalId, getGoalDtoNullSkills()));
    }

    @Test
    public void validateGoalUpdateEmptySkillsThrowsException() {
        Long goalId = 1L;
        when(goalRepository.existsById(goalId)).thenReturn(true);
        when(goalRepository.findById(goalId)).thenReturn(Optional.ofNullable(getGoalToUpdateValidStatus()));

        assertThrows(EntityFieldsException.class, ()
                -> goalValidator.validateGoalUpdate(goalId, getGoalDtoEmptySkills()));
    }

    @Test
    public void validateGoalUpdateSkillNotExistsThrowsException() {
        Long goalId = 1L;
        when(goalRepository.existsById(goalId)).thenReturn(true);
        when(goalRepository.findById(goalId)).thenReturn(Optional.ofNullable(getGoalToUpdateValidStatus()));
        when(skillRepository.existsById(goalId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, ()
                -> goalValidator.validateGoalUpdate(goalId, getValidGoalDto()));
    }

    @Test
    public void validateGoalExistsNotExistsThrowsException() {
        Long goalId = 1L;
        when(goalRepository.existsById(goalId)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, ()
                -> goalValidator.validateGoalExists(goalId));
    }

    private GoalDto getValidGoalDto() {
        return GoalDto.builder()
                .title("title")
                .skillIds(List.of(1L, 2L))
                .build();
    }

    private GoalDto getGoalDtoEmptySkills() {
        return GoalDto.builder()
                .title("title")
                .skillIds(List.of())
                .build();
    }

    private GoalDto getGoalDtoNullSkills() {
        return GoalDto.builder()
                .title("title")
                .skillIds(null)
                .build();
    }

    private GoalDto getGoalDtoEmptyTitle() {
        return GoalDto.builder()
                .title(null)
                .skillIds(List.of(1L, 2L))
                .build();
    }

    private GoalDto getGoalDtoNullTitle() {
        return GoalDto.builder()
                .title("")
                .skillIds(List.of(1L, 2L))
                .build();
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