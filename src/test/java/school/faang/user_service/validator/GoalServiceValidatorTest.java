package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.SkillService;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceValidatorTest {

    @InjectMocks
    private GoalServiceValidator goalServiceValidator;

    @Mock
    private SkillService skillService;

    private Goal goal;
    private List<Skill> skills;

    @BeforeEach
    public void setUp() {
        goal = new Goal();
        goal.setStatus(GoalStatus.ACTIVE);

        Skill skill = new Skill();
        skill.setTitle("Test Skill");
        skills = List.of(skill);
    }

    @Test
    @DisplayName("Test validateUserGoalLimit when user exceeds goal limit")
    public void testValidateUserGoalLimitWhenExceeded() {
        int activeGoalCount = 3;

        assertThrows(DataValidationException.class, () ->
                        goalServiceValidator.validateUserGoalLimit(activeGoalCount),
                "This user has exceeded the goal limit"
        );
    }

    @Test
    @DisplayName("Test validateUserGoalLimit when user does not exceed goal limit")
    public void testValidateUserGoalLimitWhenNotExceeded() {
        int activeGoalCount = 2;

        goalServiceValidator.validateUserGoalLimit(activeGoalCount);

        verifyNoMoreInteractions(skillService);
    }

    @Test
    @DisplayName("Test validateGoalsExist when goals do not exist")
    public void testValidateGoalsExistWhenEmpty() {
        Stream<Goal> emptyGoalsStream = Stream.empty();

        assertThrows(DataValidationException.class, () ->
                        goalServiceValidator.validateGoalsExist(emptyGoalsStream),
                "A goal with this ID does not exist"
        );
    }

    @Test
    @DisplayName("Test validateGoalsExist when goals exist")
    public void testValidateGoalsExistWhenNotEmpty() {
        Stream<Goal> goalsStream = Stream.of(goal);

        goalServiceValidator.validateGoalsExist(goalsStream);

        verifyNoMoreInteractions(skillService);
    }

    @Test
    @DisplayName("Test validateGoalStatusNotCompleted when goal is completed")
    public void testValidateGoalStatusNotCompletedWhenCompleted() {
        goal.setStatus(GoalStatus.COMPLETED);

        assertThrows(DataValidationException.class, () ->
                        goalServiceValidator.validateGoalStatusNotCompleted(goal),
                "The goal cannot be updated because it is already completed"
        );
    }

    @Test
    @DisplayName("Test validateGoalStatusNotCompleted when goal is not completed")
    public void testValidateGoalStatusNotCompletedWhenNotCompleted() {
        goal.setStatus(GoalStatus.ACTIVE);

        goalServiceValidator.validateGoalStatusNotCompleted(goal);

        verifyNoMoreInteractions(skillService);
    }

    @Test
    @DisplayName("Test validateSkillsExistByTitle when skills do not exist")
    public void testValidateSkillsExistByTitleWhenSkillsDoNotExist() {
        when(skillService.existsByTitle(skills)).thenReturn(false);

        assertThrows(DataValidationException.class, () ->
                        goalServiceValidator.validateSkillsExistByTitle(skills),
                "There is no skill with this name"
        );
    }

    @Test
    @DisplayName("Test validateSkillsExistByTitle when skills exist")
    public void testValidateSkillsExistByTitleWhenSkillsExist() {
        when(skillService.existsByTitle(skills)).thenReturn(true);

        goalServiceValidator.validateSkillsExistByTitle(skills);

        verify(skillService, times(1)).existsByTitle(skills);
    }
}
