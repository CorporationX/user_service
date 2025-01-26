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
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.MentorshipService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
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

    private long userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = 1L;
        user = User.builder()
                .id(userId)
                .active(true)
                .build();
    }

    @Test
    public void testGetUserSuccess() {
        User expectedUser = User.builder()
                .id(userId)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(expectedUser));

        User actualUser = userService.getUser(userId);

        assertEquals(expectedUser, actualUser);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testUserExists() {
        boolean expectedResult = true;

        when(userRepository.existsById(userId))
                .thenReturn(expectedResult);

        boolean actualResult = userService.userExists(userId);

        verify(userRepository, times(1))
                .existsById(eq(userId));

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testGetUserThrowExceptionWhenNotFound() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.getUser(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetUserByIds() {
        List<User> users = List.of(
                User.builder()
                        .id(1L)
                        .build(),
                User.builder()
                        .id(2L)
                        .build(),
                User.builder()
                        .id(3L)
                        .build()
        );
        when(userRepository.findAllById(anyList()))
                .thenReturn(users);

        List<User> actualUserList = userService.getUsersByIds(users);

        assertEquals(users.size(), actualUserList.size());
        assertEquals(users, actualUserList);
        verify(userRepository, times(1)).findAllById(anyList());
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

        assertFalse(user.isActive());
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
