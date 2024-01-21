package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.skill.SkillService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalValidatorTest {

    @Mock
    private GoalService goalService;

    @Mock
    private SkillService skillService;

    @InjectMocks
    private GoalValidator goalValidator;

    @Test
    void nullUserIdTest() {
        assertThrows(DataValidationException.class, () -> goalValidator.validateUserId(null));
    }

    @Test
    void nullGoalTitleTest() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle(null);
        assertThrows(DataValidationException.class, () -> goalValidator.validateGoalTitle(goalDto));
    }

    @Test
    void blankGoalTitleTest() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("");
        assertThrows(DataValidationException.class, () -> goalValidator.validateGoalTitle(goalDto));
    }
    @Test
    void incorrectSkillsTest() {
        List<Skill> skills = Collections.singletonList(new Skill());
        Mockito.when(skillService.validateSkill(Mockito.any())).thenReturn(false);
        assertThrows(DataValidationException.class, () -> goalValidator.validateSkills(skills));
    }
}