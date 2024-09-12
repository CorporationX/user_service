package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.user.UserDeactivatedException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalService goalService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private MentorshipService mentorshipService;
    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setActive(true);

        Goal goal1 = new Goal();
        goal1.setId(1L);
        goal1.setTitle("Goal 1");
        goal1.setUsers(new ArrayList<>(List.of(user)));

        Goal goal2 = new Goal();
        goal2.setId(2L);
        goal2.setTitle("Goal 2");
        goal2.setUsers(new ArrayList<>(List.of(user, new User())));
        user.setGoals(new ArrayList<>(List.of(goal1, goal2)));

        Event event1 = new Event();
        event1.setId(1L);
        event1.setTitle("Webinar Java");
        event1.setStatus(EventStatus.PLANNED);
        event1.setOwner(user);

        Event event2 = new Event();
        event2.setId(2L);
        event2.setTitle("Webinar Spring");
        event2.setStatus(EventStatus.COMPLETED);
        event2.setOwner(user);
        user.setOwnedEvents(new ArrayList<>(List.of(event1, event2)));
    }

    @Test
    public void deactivateUserSuccess() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        List<Goal> userGoals = new ArrayList<>(user.getGoals());
        List<Event> userEvents = new ArrayList<>(user.getOwnedEvents());

        userService.deactivateUser(userId);

        verify(userRepository).findById(userId);
        verify(goalService).deleteGoalAndUnlinkChildren(userGoals.get(0));
        verify(eventRepository).save(userEvents.get(0));
        verify(eventRepository).delete(userEvents.get(0));
        verify(mentorshipService).deleteMentorFromMentees(userId, user.getMentees());
        verify(userRepository).save(user);

        assertEquals(user.getGoals().size(), 0);
    }

    @Test
    public void deactivateUserFailed() {
        Long userId = 1L;
        user.setActive(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(UserDeactivatedException.class, () -> userService.deactivateUser(userId));
    }
}