package school.faang.user_service.service;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private User user;
    private final long userId = 1L;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(userId)
                .active(true)
                .build();
    }

    @Test
    public void testUserNotFoundThrows() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userServiceImpl.deactivateUser(userId));

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository, goalRepository, eventRepository, mentorshipService);
    }

    @Test
    public void testDeactivateDeactivatesUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userServiceImpl.deactivateUser(userId);

        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    public void testDeactivateStopsMentoring() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userServiceImpl.deactivateUser(userId);

        verify(mentorshipService).stopMentoringIfMentor(user);
    }

    @Test
    public void testStopGoalsEmpty() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.empty());

        userServiceImpl.deactivateUser(userId);

        verify(goalRepository).findGoalsByUserId(userId);
        verifyNoMoreInteractions(goalRepository);
    }

    @Test
    public void testStopGoalsSingleParticipantDeletes() {
        Goal goal = new Goal();
        goal.setId(1L);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal));
        when(goalRepository.findUsersByGoalId(goal.getId())).thenReturn(List.of(user));

        userServiceImpl.deactivateUser(userId);

        verify(goalRepository).delete(goal);
        verify(goalRepository, never()).save(goal);
    }

    @Test
    public void testStopGoalsMultipleParticipantsRemovesUser() {
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setMentor(user);
        User otherUser = User.builder().id(2L).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal));
        when(goalRepository.findUsersByGoalId(goal.getId())).thenReturn(new ArrayList<>(List.of(user, otherUser)));

        userServiceImpl.deactivateUser(userId);

        assertEquals(1, goal.getUsers().size());
        assertFalse(goal.getUsers().contains(user));
        assertNull(goal.getMentor());
        verify(goalRepository).save(goal);
        verify(goalRepository, never()).delete(goal);
    }

    @Test
    public void testStopEventsEmpty() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        userServiceImpl.deactivateUser(userId);

        verify(eventRepository, times(2)).findAllByUserId(userId);
        verify(eventRepository, never()).save(any());
        verify(eventRepository).deleteAll(Collections.emptyList());
        verifyNoMoreInteractions(eventRepository);
    }

    @Test
    public void testStopEventsCancelsAll() {
        Event event1 = new Event();
        event1.setId(1L);
        Event event2 = new Event();
        event2.setId(2L);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findAllByUserId(userId)).thenReturn(List.of(event1, event2));

        userServiceImpl.deactivateUser(userId);

        assertEquals(EventStatus.CANCELED, event1.getStatus());
        assertEquals(EventStatus.CANCELED, event2.getStatus());
        verify(eventRepository).save(event1);
        verify(eventRepository).save(event2);
    }

    @Test
    public void testFindUserExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = userServiceImpl.findUserById(userId);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    public void testFindUserNotExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userServiceImpl.findUserById(userId);

        assertTrue(result.isEmpty());
    }
}
