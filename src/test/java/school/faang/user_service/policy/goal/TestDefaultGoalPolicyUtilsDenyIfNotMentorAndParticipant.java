package school.faang.user_service.policy.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.User;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TestDefaultGoalPolicyUtilsDenyIfNotMentorAndParticipant {
    public static final long USER_ID = 5L;
    public static final long MENTOR_ID = 3L;

    @InjectMocks
    DefaultGoalPolicyUtils utils;

    @Test
    void shouldNotDenyWhenUserIsMentor() {
        User mentor = new User();
        mentor.setId(MENTOR_ID);
        Goal goal = new Goal();
        goal.setMentor(mentor);
        goal.setUsers(List.of());
        Runnable deny = mock(Runnable.class);

        utils.denyIfNotMentorAndParticipant(MENTOR_ID, goal, deny);

        verify(deny, never()).run();
    }

    @Test
    void shouldNotDenyWhenUserIsParticipant() {
        User mentor = new User();
        mentor.setId(MENTOR_ID);
        User participant1 = new User();
        participant1.setId(5L);
        User participant2 = new User();
        participant2.setId(USER_ID);
        Goal goal = new Goal();
        goal.setMentor(mentor);
        goal.setUsers(List.of(participant1, participant2));
        Runnable deny = mock(Runnable.class);

        utils.denyIfNotMentorAndParticipant(USER_ID, goal, deny);

        verify(deny, never()).run();
    }

    @Test
    void shouldDenyWhenNotMentorNorParticipant() {
        User mentor = new User();
        mentor.setId(MENTOR_ID);
        User participant = new User();
        participant.setId(200L);
        Goal goal = new Goal();
        goal.setMentor(mentor);
        goal.setUsers(List.of(participant));
        Runnable deny = mock(Runnable.class);

        utils.denyIfNotMentorAndParticipant(USER_ID, goal, deny);

        verify(deny, times(1)).run();
    }

    @Test
    void shouldDenyWhenNoMentorAndNotParticipant() {
        Goal goal = new Goal();
        goal.setMentor(null);
        goal.setUsers(List.of());
        Runnable deny = mock(Runnable.class);

        utils.denyIfNotMentorAndParticipant(USER_ID, goal, deny);

        verify(deny, times(1)).run();
    }

    @Test
    void shouldNotDenyWhenNoMentorButParticipant() {
        User participant = new User();
        participant.setId(USER_ID);
        Goal goal = new Goal();
        goal.setMentor(null);
        goal.setUsers(List.of(participant));
        Runnable deny = mock(Runnable.class);

        utils.denyIfNotMentorAndParticipant(USER_ID, goal, deny);

        verify(deny, never()).run();
    }

    @Test
    void shouldDenyWhenUsersNullAndNotMentor() {
        User mentor = new User();
        mentor.setId(MENTOR_ID);
        Goal goal = new Goal();
        goal.setMentor(mentor);
        goal.setUsers(null);
        Runnable deny = mock(Runnable.class);

        utils.denyIfNotMentorAndParticipant(USER_ID, goal, deny);

        verify(deny, times(1)).run();
    }
}
