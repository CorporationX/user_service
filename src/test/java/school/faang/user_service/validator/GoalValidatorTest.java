package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalValidatorTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private GoalMapperImpl goalMapper;
    @InjectMocks
    private GoalValidator goalValidator;

    GoalDto goalDto = GoalDto.builder().skillIds(List.of(1L)).userIds(List.of()).build();

    @Test
    void createGoalControllerValidation_nullGoal_Test() {
        Exception exception = assertThrows(DataValidationException.class, () ->
                goalValidator.createGoalControllerValidation(null));

        assertEquals(exception.getMessage(), "Goal cannot be null");
    }

    @Test
    void updateGoalControllerValidation_blankTitle_Test() {
        Exception exception = assertThrows(DataValidationException.class, () ->
                goalValidator.updateGoalControllerValidation(goalDto));
        System.out.println(goalDto.getTitle());

        assertEquals(exception.getMessage(), "Title can not be blank or null");
    }

    @Test
    void creatingGoalServiceValidation_activeGoals_Test() {
        Mockito.when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(3);

        Exception exception = assertThrows(DataValidationException.class, () ->
                goalValidator.creatingGoalServiceValidation(1L, goalDto));

        assertEquals(exception.getMessage(), "User can't have more than 3 Active goals");
    }

    @Test
    void updateCompletedGoalTest() {
        Goal goal = Goal.builder().status(GoalStatus.COMPLETED).build();
        //when(goalRepository.findById(id)).thenReturn(Optional.ofNullable(old));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalValidator.updateGoalServiceValidation(goal, goalDto));

        assertEquals("Goal already completed!", exception.getMessage());
    }

    @Test
    void updateGoalSkillFoundTest() {
        Goal goal = Goal.builder().status(GoalStatus.ACTIVE).build();

        when(skillRepository.countExisting(goalDto.getSkillIds())).thenReturn(0);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> goalValidator.updateGoalServiceValidation(goal, goalDto));

        assertEquals("Goal contains non-existent skill!", exception.getMessage());
    }

}