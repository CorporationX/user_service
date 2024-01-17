package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
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
    private SkillService skillService;
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
        Long userId = 1L;
        GoalDto goalDto = new GoalDto();
        goalDto.setSkillIds(List.of(1L));
        SkillDto skillDto_1 = SkillDto.builder().id(1L).build();
        SkillDto skillDto_2 = SkillDto.builder().id(2L).build();

        when(skillService.getUserSkills(userId)).thenReturn(List.of(skillDto_1, skillDto_2));

        assertEquals(goalValidator.isValidateByExistingSkills(userId, goalDto), true);
    }

    @Test
    void testisValidateByExistingSkillsShouldException() {
        Long userId = 1L;
        GoalDto goalDto = new GoalDto();
        goalDto.setSkillIds(List.of(1L));
        SkillDto skillDto = SkillDto.builder().id(2L).build();

        when(skillService.getUserSkills(userId)).thenReturn(List.of(skillDto));

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.isValidateByExistingSkills(1L, goalDto));

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
