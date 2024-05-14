package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalValidatorTest {

    @InjectMocks
    private GoalValidator goalValidator;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillService skillService;

    private long userId;
    private Goal goal;
    private GoalDto goalDto;

    @BeforeEach
    public void setup() {
        userId = 1L;
        goal = new Goal();
        goalDto = new GoalDto();
        goalDto.setSkillIds(List.of(1L, 2L));
    }

    @Test
    public void testValidateBeforeCreateWith3ActiveGoals() {
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(3);

        assertThrows(DataValidationException.class, () -> goalValidator.validateBeforeCreate(userId, goalDto));
    }

    @Test
    public void testValidateBeforeCreateWithNoExistSkills() {
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);
        when(skillService.existsById(anyLong())).thenReturn(false);

        assertThrows(DataValidationException.class, () -> goalValidator.validateBeforeCreate(userId, goalDto));
    }

    @Test
    public void testValidateBeforeCreateWithValidArgs() {
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);
        when(skillService.existsById(anyLong())).thenReturn(true);

        goalValidator.validateBeforeCreate(userId, goalDto);
    }

    @Test
    public void testValidateBeforeUpdateWIthStatusCompleted() {
        goal.setStatus(GoalStatus.COMPLETED);

        assertThrows(DataValidationException.class, () -> goalValidator.validateBeforeUpdate(goal, goalDto));
    }

    @Test
    public void testValidateBeforeUpdateWithStatusActive() {
        goal.setStatus(GoalStatus.ACTIVE);
        when(skillService.existsById(anyLong())).thenReturn(true);

        goalValidator.validateBeforeUpdate(goal, goalDto);
    }

    @Test
    public void testValidateGoalTitleWithEmptyTitle() {
        goalDto.setTitle("");
        assertThrows(DataValidationException.class, () -> goalValidator.validateGoalTitle(goalDto));
    }

    @Test
    public void testValidateGoalTitleWithNull() {
        assertThrows(DataValidationException.class, () -> goalValidator.validateGoalTitle(goalDto));
    }

    @Test
    public void testValidateGoalTitleWithValidTitle() {
        goalDto.setTitle("title");

        goalValidator.validateGoalTitle(goalDto);
    }
}
