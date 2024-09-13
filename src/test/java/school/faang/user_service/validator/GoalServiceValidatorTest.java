package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    private static final int GOAL_LIMIT = 3;
    private static final int ACTIVE_GOAL_COUNT_BELOW_LIMIT = 2;
    private static final GoalStatus COMPLETED_STATUS = GoalStatus.COMPLETED;
    private static final GoalStatus ACTIVE_STATUS = GoalStatus.ACTIVE;
    private static final String SKILL_TITLE = "Test Skill";

    @InjectMocks
    private GoalServiceValidator goalServiceValidator;

    @Mock
    private SkillService skillService;

    private Goal goal;
    private List<Skill> skills;

    @BeforeEach
    public void setUp() {
        goal = new Goal();
        goal.setStatus(ACTIVE_STATUS);

        Skill skill = new Skill();
        skill.setTitle(SKILL_TITLE);
        skills = List.of(skill);
    }

    @Nested
    @DisplayName("User Goal Limit Validation Tests")
    class UserGoalLimitTests {

        @Test
        @DisplayName("whenUserExceedsGoalLimitThenThrowException")
        void whenUserExceedsGoalLimitThenThrowException() {
            assertThrows(DataValidationException.class, () ->
                            goalServiceValidator.validateUserGoalLimit(GOAL_LIMIT),
                    "This user has exceeded the goal limit"
            );
        }

        @Test
        @DisplayName("whenUserDoesNotExceedGoalLimitThenDoNotThrowException")
        void whenUserDoesNotExceedGoalLimitThenDoNotThrowException() {
            goalServiceValidator.validateUserGoalLimit(ACTIVE_GOAL_COUNT_BELOW_LIMIT);

            verifyNoMoreInteractions(skillService);
        }
    }

    @Nested
    @DisplayName("Goal Existence Validation Tests")
    class GoalExistenceTests {

        @Test
        @DisplayName("whenNoGoalsExistThenThrowException")
        void whenNoGoalsExistThenThrowException() {
            Stream<Goal> emptyGoalsStream = Stream.empty();

            assertThrows(DataValidationException.class,
                    () -> goalServiceValidator.validateGoalsExist(emptyGoalsStream),
                    "A goal with this ID does not exist"
            );
        }

        @Test
        @DisplayName("whenGoalsExistThenDoNotThrowException")
        void whenGoalsExistThenDoNotThrowException() {
            Stream<Goal> goalsStream = Stream.of(goal);

            goalServiceValidator.validateGoalsExist(goalsStream);

            verifyNoMoreInteractions(skillService);
        }
    }

    @Nested
    @DisplayName("Goal Status Validation Tests")
    class GoalStatusTests {

        @Test
        @DisplayName("whenGoalIsCompletedThenThrowException")
        void whenGoalIsCompletedThenThrowException() {
            goal.setStatus(COMPLETED_STATUS);

            assertThrows(DataValidationException.class,
                    () -> goalServiceValidator.validateGoalStatusNotCompleted(goal),
                    "The goal cannot be updated because it is already completed"
            );
        }

        @Test
        @DisplayName("whenGoalIsNotCompletedThenDoNotThrowException")
        void whenGoalIsNotCompletedThenDoNotThrowException() {
            goal.setStatus(ACTIVE_STATUS);

            goalServiceValidator.validateGoalStatusNotCompleted(goal);

            verifyNoMoreInteractions(skillService);
        }
    }

    @Nested
    @DisplayName("Skills Existence Validation Tests")
    class SkillsExistenceTests {

        @Test
        @DisplayName("whenSkillsDoNotExistThenThrowException")
        void whenSkillsDoNotExistThenThrowException() {
            when(skillService.existsByTitle(skills)).thenReturn(false);

            assertThrows(DataValidationException.class,
                    () -> goalServiceValidator.validateSkillsExistByTitle(skills),
                    "There is no skill with this name"
            );
        }

        @Test
        @DisplayName("whenSkillsExistThenDoNotThrowException")
        void whenSkillsExistThenDoNotThrowException() {
            when(skillService.existsByTitle(skills)).thenReturn(true);

            goalServiceValidator.validateSkillsExistByTitle(skills);

            verify(skillService, times(1)).existsByTitle(skills);
        }
    }
}

