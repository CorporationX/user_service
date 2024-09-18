package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goal.GoalValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalMapper goalMapper;

    @Mock
    private GoalValidator goalValidator;

    @Mock
    private UserService userService;

    private GoalDto goalDto;
    private Goal goal;
    private User user;

    private static final Long USER_ID = 1L;
    private static final Long GOAL_ID = 1L;

    @BeforeEach
    public void setUp() {
        user = new User();
        goal = new Goal();
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setUsers(new ArrayList<>(List.of(user)));

        goalDto = new GoalDto();
        goalDto.setTitle("New Goal");
        goalDto.setDescription("Goal Description");
    }

    @Nested
    @DisplayName("User Goal Limit Validation Tests")
    class UserGoalLimitTests {

        @Test
        @DisplayName("Throws exception when user exceeds goal limit during goal creation")
        void whenUserExceedsGoalLimitThenThrowExceptionOnCreate() {
            doThrow(DataValidationException.class).when(goalValidator).validateUserGoalLimit(USER_ID);

            assertThrows(DataValidationException.class, () ->
                    goalService.createGoal(USER_ID, goalDto)
            );

            verify(goalValidator, times(1)).validateUserGoalLimit(USER_ID);
        }

        @Test
        @DisplayName("Does not throw exception when user does not exceed goal limit during goal creation")
        void whenUserDoesNotExceedGoalLimitThenDoNotThrowExceptionOnCreate() {
            when(userService.getUserById(USER_ID)).thenReturn(user);
            when(goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), null)).thenReturn(goal);
            when(goalMapper.toGoalDto(goal)).thenReturn(goalDto);

            GoalDto result = goalService.createGoal(USER_ID, goalDto);

            assertNotNull(result);
            verify(goalValidator, times(1)).validateUserGoalLimit(USER_ID);
            verify(goalMapper, times(1)).toGoalDto(goal);
        }
    }

    @Nested
    @DisplayName("Goal Status Validation Tests")
    class GoalStatusTests {

        @Test
        @DisplayName("Throws exception when goal is completed during update")
        void whenGoalIsCompletedThenThrowExceptionOnUpdate() {
            goal.setStatus(GoalStatus.COMPLETED);

            when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(goal));
            doThrow(DataValidationException.class).when(goalValidator).validateGoalStatusNotCompleted(goal);

            assertThrows(DataValidationException.class, () ->
                    goalService.updateGoal(GOAL_ID, goalDto)
            );

            verify(goalValidator, times(1)).validateGoalStatusNotCompleted(goal);
        }

        @Test
        @DisplayName("Does not throw exception when goal is not completed during update")
        void whenGoalIsNotCompletedThenDoNotThrowExceptionOnUpdate() {
            goal.setStatus(GoalStatus.ACTIVE);

            when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(goal));
            when(goalMapper.toGoalDto(goal)).thenReturn(goalDto);

            GoalDto result = goalService.updateGoal(GOAL_ID, goalDto);

            assertNotNull(result);
            verify(goalValidator, times(1)).validateGoalStatusNotCompleted(goal);
            verify(goalMapper, times(1)).toGoalDto(goal);
        }
    }

    @Test
    @DisplayName("Throws exception when goal is not found")
    void whenGoalNotFoundThenThrowEntityNotFoundException() {
        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                goalService.updateGoal(GOAL_ID, goalDto)
        );

        verify(goalRepository, times(1)).findById(GOAL_ID);
    }
}