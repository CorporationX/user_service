package school.faang.user_service.validator.goal;

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
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Ilia Chuvatkin
 */

@ExtendWith(MockitoExtension.class)
public class GoalValidatorTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillService skillService;
    @InjectMocks
    private GoalValidator goalValidator;

    @Test
    void testValidateActiveGoalsShouldException() {
        long countActiveGoals = 4L;

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.validateActiveGoals(countActiveGoals));
        assertEquals(dataValidationException.getMessage(), "Too many active goals!");
    }

    @Test
    void testValidateExistingSkillsShouldException() {
        GoalDto goalDto = new GoalDto();
        goalDto.setSkillIds(List.of(1L));
        List<Long> userSkillsIds = List.of(2L, 3L);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.validateExistingSkills(userSkillsIds, goalDto));

        assertEquals(dataValidationException.getMessage(), "Not enough skills for the goal!");
    }

    @Test
    void testValidateEmptyTitleWithBlankShouldException() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle(" ");

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.validateTitle(goalDto));

        assertEquals(dataValidationException.getMessage(), "Title is empty!");
    }

    @Test
    void testValidateEmptyTitleWithNullShouldException() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle(null);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.validateTitle(goalDto));

        assertEquals(dataValidationException.getMessage(), "Title is empty!");
    }

    @Test
    void testValidateUserIdWithNullShouldException() {
        Long userId = null;

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.validateUserId(userId));

        assertEquals(dataValidationException.getMessage(), "User ID required!");
    }

    @Test
    void testValidateTitleAndGoalIdWhenTitleEmptyShouldException() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle(" ");
        Long goalId = 1L;

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.validateTitleAndGoalId(goalId, goalDto));

        assertEquals(dataValidationException.getMessage(), "Title is empty!");
    }

    @Test
    void testValidateTitleAndGoalIdWhenGoalIdNullShouldException() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");
        Long goalId = null;

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.validateTitleAndGoalId(goalId, goalDto));

        assertEquals(dataValidationException.getMessage(), "Goal ID is null!");
    }

    @Test
    void testIsValidateByCompletedShouldException() {
        Goal goal = new Goal();
        goal.setStatus(GoalStatus.COMPLETED);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.validateByCompleted(goal));

        assertEquals(dataValidationException.getMessage(), "Goal was completed!");
    }

    @Test
    void testIsValidateByExistingSkillsShouldSuccess() {
        Goal goal = new Goal();
        Skill skill_1 = new Skill();
        skill_1.setTitle("Skill_1");
        Skill skill_2 = new Skill();
        skill_2.setTitle("Skill_2");
        goal.setSkillsToAchieve(List.of(skill_1, skill_2));

        when(skillRepository.existsByTitle(anyString())).thenReturn(true);

        goalValidator.validateByExistingSkills(goal);

        verify(skillRepository, times(2)).existsByTitle(anyString());
    }

    @Test
    void testIsValidateByExistingSkillsShouldException() {
        Goal goal = new Goal();
        Skill skill_1 = new Skill();
        skill_1.setTitle("Skill_1");
        Skill skill_2 = new Skill();
        skill_2.setTitle("Skill_2");
        goal.setSkillsToAchieve(List.of(skill_1, skill_2));


        when(skillRepository.existsByTitle(anyString())).thenReturn(false);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.validateByExistingSkills(goal));

        assertEquals(dataValidationException.getMessage(), "Some skills do not exist in database!");
    }

    @Test
    void testValidateGoalId() {
        long goalId = 1L;
        when(goalRepository.existsById(goalId)).thenReturn(false);

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> goalValidator.validateGoalId(goalId));

        assertEquals(entityNotFoundException.getMessage(), "Goal with id = 1 is not exists");
    }
}
