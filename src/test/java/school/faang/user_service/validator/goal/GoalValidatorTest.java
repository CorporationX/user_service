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
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * @author Ilia Chuvatkin
 */

@ExtendWith(MockitoExtension.class)
public class GoalValidatorTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private GoalValidator goalValidator;

    @Test
    void testIsValidateByActiveGoalsShouldSuccess() {
        User user = new User();
        user.setId(1L);

        when(goalRepository.countActiveGoalsPerUser(user.getId())).thenReturn(2);

        assertEquals(goalValidator.isValidateByActiveGoals(1L), true);
    }

    @Test
    void testIsValidateByActiveGoalsShouldException() {
        User user = new User();
        user.setId(1L);

        when(goalRepository.countActiveGoalsPerUser(user.getId())).thenReturn(4);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.isValidateByActiveGoals(1L));
        assertEquals(dataValidationException.getMessage(), "Too many active goals!");
    }

    @Test
    void testisValidateByExistingSkillsShouldSuccess() {
        User user = new User();
        user.setId(1L);
        Goal goal = new Goal();
        Skill skill_1 = new Skill();
        skill_1.setId(1L);
        Skill skill_2 = new Skill();
        skill_2.setId(2L);
        goal.setSkillsToAchieve(List.of(skill_1));

        when(skillRepository.findAllByUserId(user.getId())).thenReturn(List.of(skill_1, skill_2));

        assertEquals(goalValidator.isValidateByExistingSkills(1L, goal), true);
    }

    @Test
    void testisValidateByExistingSkillsShouldException() {
        User user = new User();
        user.setId(1L);
        Goal goal = new Goal();
        Skill skill_1 = new Skill();
        skill_1.setId(1L);
        Skill skill_2 = new Skill();
        skill_2.setId(2L);
        goal.setSkillsToAchieve(List.of(skill_1));

        when(skillRepository.findAllByUserId(user.getId())).thenReturn(List.of(skill_2));

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.isValidateByExistingSkills(1L, goal));

        assertEquals(dataValidationException.getMessage(), "Not enough skills for the goal!");
    }

    @Test
    void testIsValidateByEmptyTitleShouldSuccess() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Goals title");

        assertEquals(goalValidator.isValidateByEmptyTitle(goalDto), true);
    }

    @Test
    void testIsValidateByEmptyTitleShouldException() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle(" ");

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.isValidateByEmptyTitle(goalDto));

        assertEquals(dataValidationException.getMessage(), "Title is empty!");
    }
}
