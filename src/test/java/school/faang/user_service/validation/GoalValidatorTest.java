package school.faang.user_service.validation;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GoalValidatorTest {

    @Mock
    private UserService userService;
    @Mock
    private SkillService skillService;
    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    GoalValidator goalValidator;

    @Value("${goal.max_active_goals}")
    int maxActiveGoals = 2;
    Long userId = 1L;
    GoalDto goalDto = GoalDto.builder()
            .id(1L)
            .status(GoalStatus.ACTIVE)
            .skillsToAchieveIds(List.of(1L))
            .build();
    Goal completedGoal = Goal.builder()
            .status(GoalStatus.COMPLETED)
            .build();

    @Test
    void validateCreationWithNotValidUserShouldThrowsException() {
        when(userService.existsById(userId)).thenReturn(false);
        Exception exception = assertThrows(DataValidationException.class, () -> goalValidator.validateCreation(userId, goalDto));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void validateCreationWithActiveGoalsCountNotValidShouldThrowsException() {
        when(userService.existsById(userId)).thenReturn(true);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(3);
        Exception exception = assertThrows(DataValidationException.class, () -> goalValidator.validateCreation(userId, goalDto));
        assertEquals("Active goals count exceeded", exception.getMessage());
    }

    @Test
    void validateCreationWithSkillsNotExistShouldThrowsException() {
        when(userService.existsById(userId)).thenReturn(true);
        when(skillService.existsById(1L)).thenReturn(false);
        Exception exception = assertThrows(DataValidationException.class, () -> goalValidator.validateCreation(userId, goalDto));
        assertEquals("Skill not found", exception.getMessage());
    }

    @Test
    void validateUpdatingWithNotValidGoalIdShouldThrowsException() {
        when(goalRepository.existsById(goalDto.getId())).thenReturn(false);
        Exception exception = assertThrows(DataValidationException.class, () -> goalValidator.validateUpdating(1L, goalDto));
        assertEquals("Goal not found", exception.getMessage());
    }

    @Test
    void validateUpdatingWithGoalStatusAlreadyCompletedShouldThrowsException() {
        when(goalRepository.existsById(goalDto.getId())).thenReturn(true);
        when(goalRepository.findById(goalDto.getId())).thenReturn(Optional.of(completedGoal));
        Exception exception = assertThrows(DataValidationException.class, () -> goalValidator.validateUpdating(1L, goalDto));
        assertEquals("Goal already completed", exception.getMessage());
    }

    @Test
    void validateUpdatingWithSkillsNotExistShouldThrowsException() {
        when(goalRepository.existsById(1L)).thenReturn(true);
        when(skillService.existsById(1L)).thenReturn(false);
        Exception exception = assertThrows(DataValidationException.class, () -> goalValidator.validateUpdating(1L, goalDto));
        assertEquals("Skill not found", exception.getMessage());
    }

    @Test
    void validateUserExistenceWithUserNotExistShouldThrowsException() {
        when(userService.existsById(userId)).thenReturn(false);
        Exception exception = assertThrows(DataValidationException.class, () -> goalValidator.validateUserExistence(userId));
        assertEquals("User not found", exception.getMessage());
    }
}
