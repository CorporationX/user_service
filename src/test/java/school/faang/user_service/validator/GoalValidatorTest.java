package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class GoalValidatorTest {
    private GoalDto goalDto;
    private Long userId;

    @InjectMocks
    private GoalValidator goalValidator;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @BeforeEach
    public void setUp() {
        goalDto = new GoalDto();
        userId = 1L;
    }

    @Test
    public void testValidateGoalWithBlankTitle() {
        goalDto.setTitle("  ");
        ValidationException thrown = assertThrows(ValidationException.class,
                () -> goalValidator.validateGoalTitle(goalDto));
        assertEquals("Title cannot be empty", thrown.getMessage());
    }

    @Test
    public void testValidateGoalsPerUserWithTooManyGoals() {
        doReturn(5).when(goalRepository).countActiveGoalsPerUser(userId);
        ValidationException thrown = assertThrows(ValidationException.class,
                () -> goalValidator.validateGoalsPerUser(userId));
        assertEquals("There cannot be more than 3 active goals per user", thrown.getMessage());
    }

    @Test
    public void testValidateGoalSkillsWithNoExistingSkills() {
        goalDto.setSkillIds(List.of(1L, 2L));
        doReturn(3).when(skillRepository).countExisting(goalDto.getSkillIds());
        ValidationException thrown = assertThrows(ValidationException.class,
                () -> goalValidator.validateGoalSkills(goalDto.getSkillIds()));
        assertEquals("Cannot create goal with non-existent skills", thrown.getMessage());
    }
}
