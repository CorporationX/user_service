package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.validator.goal.GoalValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Ilia Chuvatkin
 */

@ExtendWith(MockitoExtension.class)
public class GoalValidatorTest {
    @InjectMocks
    private GoalValidator goalValidator;

    @Test
    void testValidateActiveGoalsShouldException() {
        long countActiveGoals = 3L;

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
    void testValidateTitleAndUserIdWhenTitleIsBlankShouldException() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle(" ");
        Long userId = 1L;

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.validateTitleAndUserId(goalDto, userId));

        assertEquals(dataValidationException.getMessage(), "Title is empty!");
    }

    @Test
    void testValidateTitleAndUserIdWhenEmptyTitleShouldException() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle(null);
        Long userId = 1L;

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.validateTitleAndUserId(goalDto, userId));

        assertEquals(dataValidationException.getMessage(), "Title is empty!");
    }

    @Test
    void testValidateTitleAndUserIdWhenUserIdNullShouldException() {
        Long userId = null;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");


        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.validateTitleAndUserId(goalDto, userId));

        assertEquals(dataValidationException.getMessage(), "User ID is null!");
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
    void testValidateGoalIdWhenGoalIdNull() {
        Long goalId = null;

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.validateGoalId(goalId));

        assertEquals(dataValidationException.getMessage(), "Goal ID is null!");
    }

    @Test
    void testValidateByCompletedShouldException() {
        Goal goal = new Goal();
        goal.setStatus(GoalStatus.COMPLETED);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.validateByCompleted(goal));

        assertEquals(dataValidationException.getMessage(), "You can't update completed goal!");
    }

    @Test
    void testValidateGoalIdAndFilterWhenGoalIdEqual0() {
        long goalId = 0;
        GoalFilterDto filter = new GoalFilterDto();

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.validateGoalIdAndFilter(goalId, filter));

        assertEquals(dataValidationException.getMessage(), "Goal ID is 0!");
    }

    @Test
    void testValidateGoalIdAndFilterWhenFilterNull() {
        long goalId = 1L;
        GoalFilterDto filter = null;

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.validateGoalIdAndFilter(goalId, filter));

        assertEquals(dataValidationException.getMessage(), "Filter is null!");
    }

    @Test
    void testValidateUserIdAndFilterWhenUserIdNull() {
        Long userId = null;
        GoalFilterDto filter = new GoalFilterDto();

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.validateUserIdAndFilter(userId, filter));

        assertEquals(dataValidationException.getMessage(), "User ID is null!");
    }

    @Test
    void testValidateUserIdAndFilterWhenFilterNull() {
        long goalId = 1L;
        GoalFilterDto filter = null;

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalValidator.validateUserIdAndFilter(goalId, filter));

        assertEquals(dataValidationException.getMessage(), "Filter is null!");
    }
}
