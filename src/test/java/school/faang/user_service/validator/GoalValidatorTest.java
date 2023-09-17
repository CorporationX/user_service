package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GoalValidatorTest {
    @InjectMocks
    private GoalValidator goalValidator;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;
    private GoalDto goalDto;
    private Goal goal;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(goalValidator, "maxUserGoalsCount", 3);

        goalDto = GoalDto.builder()
                .id(1L)
                .skillIds(List.of(1L))
                .build();

        goal = Goal.builder()
                .id(1L)
                .status(GoalStatus.ACTIVE)
                .build();

        when(skillRepository.existsById(anyLong())).thenReturn(true);
    }

    @Test
    void validateToCreate_shouldThrowDataValidationException_whenUserHasMoreThan3ActiveGoals() {
        when(goalRepository.countActiveGoalsPerUser(anyLong())).thenReturn(4);
        assertThrows(DataValidationException.class,
                () -> goalValidator.validateToCreate(1L, goalDto));
    }

    @Test
    void validateToCreate_shouldThrowDataValidationException_whenSkillDoesNotExist() {
        when(skillRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(DataValidationException.class,
                () -> goalValidator.validateToCreate(1L, goalDto),
                "Skill with id 1 does not exist");
    }

    @Test
    void validateToCreate_shouldNotThrowAnyException() {
        assertDoesNotThrow(() -> goalValidator.validateToCreate(1L, goalDto));
    }

    @Test
    void validateToUpdate_shouldThrowDataValidationException_whenGoalIsCompleted() {
        goal.setStatus(GoalStatus.COMPLETED);
        assertThrows(DataValidationException.class,
                () -> goalValidator.validateToUpdate(goal, goalDto),
                "You cannot update completed goal");
    }

    @Test
    void validateToUpdate_shouldThrowDataValidationException_whenSkillDoesNotExist() {
        when(skillRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(DataValidationException.class,
                () -> goalValidator.validateToUpdate(goal, goalDto),
                "Skill with id 1 does not exist");
    }

    @Test
    void validateToUpdate_shouldNotThrowAnyException() {
        assertDoesNotThrow(() -> goalValidator.validateToUpdate(goal, goalDto));
    }
}