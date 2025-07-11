package school.faang.user_service.policy.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TestDefaultGoalUpdatePolicy {
    public static final long CURRENT_USER_ID = 5L;
    @InjectMocks
    DefaultGoalUpdatePolicy policy;
    @Mock
    private UserContext userContext;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    GoalPolicyUtils goalPolicyUtils;

    @BeforeEach
    void init() {
        Mockito.when(userContext.getUserId()).thenReturn(CURRENT_USER_ID);
    }

    @Test
    public void testUpdateCompletedGoalException() {
        UpdateGoalDto dto = new UpdateGoalDto(
                null, null,
                null, null,
                null, null
        );
        Goal goal = createGoal(1L, null, 0, 0);
        goal.setStatus(GoalStatus.COMPLETED);

        assertThrows(DataValidationException.class, () -> policy.validate(dto, goal));
        verify(userContext, times(1)).getUserId();
    }

    @Test
    public void testUpdateGoalByMentor() {
        UpdateGoalDto dto = new UpdateGoalDto(
                null, null,
                null, null,
                null, null
        );
        Goal goal = createGoal(1L, CURRENT_USER_ID, 0, 0);

        assertDoesNotThrow(() -> policy.validate(dto, goal));
        verify(userContext, times(1)).getUserId();
    }

    @Test
    public void testUpdateGoalByParticipant() {
        UpdateGoalDto dto = new UpdateGoalDto(
                null, null,
                null, null,
                null, null
        );
        Goal goal = createGoal(1L, null, (int) CURRENT_USER_ID, 0);

        assertDoesNotThrow(() -> policy.validate(dto, goal));
        verify(userContext, times(1)).getUserId();
    }

    private Goal createGoal(Long goalId, Long mentorId, int participantCount, int skillsCount) {
        Goal goal = new Goal();
        goal.setId(goalId);
        goal.setStatus(GoalStatus.ACTIVE);
        if (mentorId != null) {
            User mentor = new User();
            mentor.setId(mentorId);
            goal.setMentor(mentor);
        }
        if (skillsCount > 0) {
            List<Skill> skills = new ArrayList<>();
            for (long i = 1; i <= skillsCount; i++) {
                Skill skill = new Skill();
                skill.setId(i);
                skills.add(skill);
            }
            goal.setSkillsToAchieve(skills);
        }
        if (participantCount > 0) {
            List<User> users = new ArrayList<>();
            for (long i = 1; i <= participantCount; i++) {
                User newUser = new User();
                newUser.setId(i);
                users.add(newUser);
            }
            goal.setUsers(users);
        }
        return goal;
    }

}

