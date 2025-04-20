package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapperImpl;
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

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private final long userId = 1L;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(userId)
                .active(true)
                .build();
        
        userDto = new UserDto();
        userDto.setId(userId);
        userDto.setActive(true);
    }

    @Test
    public void testUserNotFoundThrows() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.deactivateUser(userId));

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository, goalRepository, eventRepository, mentorshipService);
    }

    @Test
    public void testDeactivateDeactivatesUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deactivateUser(userId);

        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    public void testDeactivateStopsMentoring() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deactivateUser(userId);

        verify(mentorshipService).stopMentoringIfMentor(user);
    }

    @Test
    public void testStopGoalsEmpty() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.empty());

        userService.deactivateUser(userId);

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

        userService.deactivateUser(userId);

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

        userService.deactivateUser(userId);

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

        userService.deactivateUser(userId);

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

        userService.deactivateUser(userId);

        assertEquals(EventStatus.CANCELED, event1.getStatus());
        assertEquals(EventStatus.CANCELED, event2.getStatus());
        verify(eventRepository).save(event1);
        verify(eventRepository).save(event2);
    }

    @Test
    public void testFindUserExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findUserById(userId);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    public void testFindUserNotExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userService.findUserById(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetUserByIdSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        UserDto result = userService.getUserById(1L);

        verify(userRepository, times(1)).findById(1L);
        assertEquals(result, userDto);
    }

    @Test
    public void testGetUserByIdWithNoResult() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    public void testBanUserSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        userService.banUser("1");

        assertTrue(user.isBanned());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testBanUserWithWrongUserId() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        userService.banUser("1");

        assertFalse(user.isBanned());
        verify(userRepository, times(0)).save(user);
    }
}