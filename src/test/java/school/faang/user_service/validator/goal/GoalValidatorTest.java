package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.skill.SkillService;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalValidatorTest {
    @Mock
    private GoalService goalService;
    @Mock
    private SkillService skillService;

    @InjectMocks
    private GoalValidator goalValidator;

    private final long userId = 1L;
    private final GoalDto goalDto = new GoalDto();
    private final int maxGoalsPerUser = 3;

    @BeforeEach
    public void init() {
        goalDto.setSkillIds(new ArrayList<>(Collections.singleton(1L)));
    }

    @Test
    void maxGoalsPerUserExceptionTest() {
        Mockito.when(goalService.countActiveGoalsPerUser(userId))
                .thenReturn(maxGoalsPerUser);
        assertThrows(DataValidationException.class,
                () -> goalValidator.validate(userId, goalDto));

    }

    @Test
    void uncorrectSkillExceptionTest() {
        Mockito.when(goalService.countActiveGoalsPerUser(userId))
                .thenReturn(maxGoalsPerUser - 1);
        Mockito.when(skillService.existsById(1L))
                .thenReturn(false);
        assertThrows(DataValidationException.class,
                () -> goalValidator.validate(userId, goalDto));
    }

    @Test
    void nullUserIdExceptionTest() {
        assertThrows(DataValidationException.class,
                () -> goalValidator.validateUserId(null));
    }

    @Test
    void nullGoalTitleExceptionTest() {
        goalDto.setTitle(null);
        assertThrows(DataValidationException.class,
                () -> goalValidator.validateGoalTitle(goalDto));
    }

    @Test
    void allCorrectTest() {
        Mockito.when(goalService.countActiveGoalsPerUser(userId))
                .thenReturn(maxGoalsPerUser - 1);
        Mockito.when(skillService.existsById(1L))
                .thenReturn(true);
        assertDoesNotThrow(() -> goalValidator.validate(userId, goalDto));
    }
}