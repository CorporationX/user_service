package school.faang.user_service.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.GoalValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

public class GoalValidatorTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private GoalValidator goalValidator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidateGoal_WithValidGoal() {
        Long userId = 1L;
        List<Long> skillIds = Arrays.asList(1L, 2L);
        Skill skill1 = new Skill();
        skill1.setId(1L);
        Skill skill2 = new Skill();
        skill2.setId(2L);
        List<Skill> skills = Arrays.asList(skill1,skill2);
        GoalDto goal = new GoalDto();
        goal.setTitle("Valid Goal");
        goal.setSkillIds(skillIds);

        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);
        when(skillRepository.findAllById(skillIds)).thenReturn(skills);

        assertDoesNotThrow(() -> goalValidator.validateGoal(userId, goal));

        verify(goalRepository, times(1)).countActiveGoalsPerUser(userId);
        verify(skillRepository, times(1)).findAllById(skillIds);
    }

    @Test
    public void testValidateGoal_WithEmptyTitle() {
        Long userId = 1L;
        GoalDto goal = new GoalDto();
        goal.setTitle("");
        goal.setSkillIds(Collections.singletonList(1L));

        assertThrows(GoalValidationException.class, () -> goalValidator.validateGoal(userId, goal));
    }

    @Test
    public void testValidateGoal_WithExceedingActiveGoalsLimit() {
        Long userId = 1L;
        List<Long> skillIds = Collections.singletonList(1L);
        GoalDto goal = new GoalDto();
        goal.setTitle("Valid Goal");
        goal.setSkillIds(skillIds);

        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(3);

        assertThrows(GoalValidationException.class, () -> goalValidator.validateGoal(userId, goal));
    }

    @Test
    public void testValidateGoal_WithNullSkillIds() {
        Long userId = 1L;
        GoalDto goal = new GoalDto();
        goal.setTitle("Valid Goal");
        goal.setSkillIds(null);

        assertThrows(GoalValidationException.class, () -> goalValidator.validateGoal(userId, goal));
    }

    @Test
    public void testValidateGoal_WithNonExistentSkill() {
        Long userId = 1L;
        List<Long> skillIds = Arrays.asList(1L, 2L);
        GoalDto goal = new GoalDto();
        goal.setTitle("Valid Goal");
        goal.setSkillIds(skillIds);

        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);
        when(skillRepository.findAllById(skillIds)).thenReturn(Collections.singletonList(new Skill()));

        assertThrows(GoalValidationException.class, () -> goalValidator.validateGoal(userId, goal));
    }
}
