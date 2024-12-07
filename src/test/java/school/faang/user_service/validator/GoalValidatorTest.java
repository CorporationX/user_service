package school.faang.user_service.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static school.faang.user_service.validator.GoalValidator.MAX_USER_GOALS_SIZE;

@ExtendWith(MockitoExtension.class)
public class GoalValidatorTest {

    @Mock
    private GoalRepository goalRepository;

    @Spy
    @InjectMocks
    private GoalValidator validator;

    GoalDto goalDto;
    User user;
    Goal goal;

    @BeforeEach
    void setUp() {
        goalDto = new GoalDto();
        user = new User();
        goal = new Goal();
    }

    @Test
    public void testValidateCreate_Successfully() {
        doNothing().when(validator).validateUser(any(User.class));
        doNothing().when(validator).validateTitle(any(GoalDto.class));
        doNothing().when(validator).validateDescription(any(GoalDto.class));

        validator.validateCreate(goalDto, user);

        verify(validator, times(1)).validateUser(user);
        verify(validator, times(1)).validateTitle(goalDto);
        verify(validator, times(1)).validateDescription(goalDto);
    }

    @Test
    public void testValidateCreate_NotValidUser() {
        doThrow(new IllegalArgumentException()).when(validator).validateUser(any(User.class));

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateCreate(goalDto, user));
    }

    @Test
    public void testValidateCreate_NotValidTitle() {
        doNothing().when(validator).validateUser(any(User.class));
        doThrow(new IllegalArgumentException()).when(validator).validateTitle(any(GoalDto.class));

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateCreate(goalDto, user));
    }

    @Test
    public void testValidateCreate_NotValidDescription() {
        doNothing().when(validator).validateUser(any(User.class));
        doNothing().when(validator).validateTitle(any(GoalDto.class));
        doThrow(new IllegalArgumentException()).when(validator).validateDescription(any(GoalDto.class));

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateCreate(goalDto, user));
    }

    @Test
    public void testValidateUpdate_Successfully() {
        doNothing().when(validator).validateGoalOnUpdate(any(Goal.class));
        doNothing().when(validator).validateTitle(any(GoalDto.class));
        doNothing().when(validator).validateDescription(any(GoalDto.class));

        validator.validateUpdate(goal, goalDto);

        verify(validator, times(1)).validateGoalOnUpdate(goal);
        verify(validator, times(1)).validateTitle(goalDto);
        verify(validator, times(1)).validateDescription(goalDto);
    }

    @Test
    public void testValidateUpdate_NotValidGoal() {
        doThrow(new IllegalArgumentException()).when(validator).validateGoalOnUpdate(any(Goal.class));

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateUpdate(goal, goalDto));
    }

    @Test
    public void testValidateUpdate_NotValidTitle() {
        doNothing().when(validator).validateGoalOnUpdate(any(Goal.class));
        doThrow(new IllegalArgumentException()).when(validator).validateTitle(any(GoalDto.class));

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateUpdate(goal, goalDto));
    }

    @Test
    public void testValidateUpdate_NotValidDescription() {
        doNothing().when(validator).validateGoalOnUpdate(any(Goal.class));
        doNothing().when(validator).validateTitle(any(GoalDto.class));
        doThrow(new IllegalArgumentException()).when(validator).validateDescription(any(GoalDto.class));

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateUpdate(goal, goalDto));
    }

    @Test
    public void testValidateUser_Successfully() {
        User user = User.builder()
                .goals(List.of())
                .build();

        validator.validateUser(user);
    }

    @Test
    public void testValidateUser_TooManyGoals() {
        List<Goal> goals = Stream.generate(Goal::new)
                .limit(MAX_USER_GOALS_SIZE)
                .toList();
        User user = User.builder()
                .goals(goals)
                .build();

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateUser(user));
    }

    @Test
    public void testValidateGoalOnUpdate_Successfully() {
        validator.validateGoalOnUpdate(goal);
    }

    @Test
    public void testValidateGoalOnUpdate_GoalAlreadyCompleted() {
        goal = Goal.builder()
                .status(GoalStatus.COMPLETED)
                .build();

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateGoalOnUpdate(goal));
    }

    @Test
    public void testValidateTitle_Successfully() {
        goalDto.setTitle("123");
        when(goalRepository.existsGoalByTitle(anyString())).thenReturn(false);

        validator.validateTitle(goalDto);
    }

    @Test
    public void testValidateTitle_TitleAlreadyExist() {
        goalDto.setTitle("123");
        when(goalRepository.existsGoalByTitle(anyString())).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateTitle(goalDto));
    }

    @Test
    public void testValidateDescription_Successfully() {
        goalDto.setDescription("123");
        when(goalRepository.existsGoalByDescription(anyString())).thenReturn(false);

        validator.validateDescription(goalDto);
    }

    @Test
    public void testValidateDescription_DescriptionAlreadyExist() {
        goalDto.setDescription("123");
        when(goalRepository.existsGoalByDescription(anyString())).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateDescription(goalDto));
    }
}
