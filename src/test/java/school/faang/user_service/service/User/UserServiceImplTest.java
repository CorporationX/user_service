package school.faang.user_service.service.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.Mentorship.MentorshipServiceImpl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class UserServiceImplTest {
    @Mock
    private MentorshipServiceImpl mentorshipService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Spy
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private User user2;
    private Goal goal;
    private Event event;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        user2 = new User();
        user2.setId(2L);

        goal = new Goal();
        goal.setId(1L);

        event = new Event();
        event.setId(1L);

        user.setOwnedEvents(List.of(event));
        user.setMentees(List.of(user2));
        user.setGoals(List.of(goal));
        user2.setMentors(List.of(user));
        user2.setGoals(List.of(goal));
    }

    @Test
    void testDeactivateNullUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.deactivateUser(2L));
    }

    @Test
    void testDeactivateUserWithMultiUsersPerGoal() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(goalRepository.findUsersByGoalId(1L)).thenReturn(List.of(user2, user));

        userService.deactivateUser(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).findUsersByGoalId(1L);
        verify(mentorshipService, times(1)).stopMentorship(1L, 2L);
    }

    @Test
    void testDeactivateUserWithOneUserPerGoal() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(goalRepository.findUsersByGoalId(1L)).thenReturn(List.of(user));

        userService.deactivateUser(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).findUsersByGoalId(1L);
        verify(goalRepository, times(1)).delete(goal);
        verify(mentorshipService, times(1)).stopMentorship(1L, 2L);
    }
}