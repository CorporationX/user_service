package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
