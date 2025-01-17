package school.faang.user_service.service;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private static MentorshipService mentorshipService;
    @Mock
    private static UserRepository userRepository;
    @Mock
    private static EventRepository eventRepository;
    @Mock
    private static GoalRepository goalRepository;

    @InjectMocks
    private UserService userService;

    private static User user;
    private static Long userId;

    @BeforeAll
    static void setUp() {
        userId = 1L;
        user = new User();
        user.setId(userId);
        user.setActive(true);
    }

    @Test
    public void testDeactivateUser_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.deactivateUser(userId));
    }

    @Test
    public void testDeactivateUser() {
        Goal goal = new Goal();
        goal.setUsers(Collections.singletonList(user));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal));
        when(eventRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());
        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(Collections.emptyList());

        userService.deactivateUser(userId);

        verify(userRepository).findById(userId);
        verify(goalRepository).findGoalsByUserId(userId);
        verify(eventRepository).findAllByUserId(userId);
        verify(eventRepository).findParticipatedEventsByUserId(userId);
        verify(userRepository).save(user);
        verify(mentorshipService).stopUserMentorship(userId);
    }

    @Test
    public void testRemoveUserFromGoals() {
        Goal goal = new Goal();
        goal.setUsers(Collections.singletonList(user));
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal));

        userService.deactivateUser(userId);

        verify(goalRepository).delete(goal);
    }

    @Test
    public void testRemoveUserEvents() {
        Event event = new Event();
        event.setStatus(EventStatus.PLANNED);
        event.setAttendees(Collections.singletonList(user));
        when(eventRepository.findAllByUserId(userId)).thenReturn(Collections.singletonList(event));
        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(Collections.singletonList(event));

        userService.deactivateUser(userId);

        verify(eventRepository).save(event);
        verify(eventRepository, times(2)).save(event);
    }
}
