package school.faang.user_service.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private MentorshipService mentorshipService;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;
    private Goal firstGoal;
    private Goal secondGoal;
    private Event firstEvent;
    private Event secondEvent;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setActive(true);
        user.setGoals(new ArrayList<>());
        user.setOwnedEvents(new ArrayList<>());
        user.setParticipatedEvents(new ArrayList<>());

        userDto = new UserDto();
        userDto.setId(user.getId());

        firstGoal = new Goal();
        firstGoal.setId(1L);
        firstGoal.setUsers(new ArrayList<>(Arrays.asList(user)));

        secondGoal = new Goal();
        secondGoal.setId(2L);
        secondGoal.setUsers(new ArrayList<>(Arrays.asList(new User())));

        firstEvent = new Event();
        firstEvent.setId(1L);
        firstEvent.setAttendees(List.of(user));

        secondEvent = new Event();
        secondEvent.setId(2L);
        secondEvent.setAttendees(List.of(user));
    }

    @Test
    public void testDeactivateUser_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> userService.deactivateUser(userDto));
        assertEquals("Пользователь с ID 1 не найден", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeactivateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);
        UserDto result = userService.deactivateUser(userDto);
        assertNotNull(result);
        assertFalse(user.isActive());
        verify(mentorshipService, times(1)).stopMentorship(user);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    public void testStopUserActivities() {
        user.setGoals(List.of(firstGoal, secondGoal));
        user.setOwnedEvents(List.of(firstEvent));
        user.setParticipatedEvents(List.of(secondEvent));
        userService.stopUserActivities(user);
        verify(eventRepository, times(1)).deleteAllById(any());
    }

    @Test
    public void testStopGoals_OnlyUserInGoal() {
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setUsers(Collections.singletonList(user));
        user.setGoals(new ArrayList<>(Collections.singletonList(goal)));
        userService.stopGoals(user);
        verify(goalRepository, times(1)).deleteById(1L);
        assertTrue(user.getGoals().isEmpty());
    }

    @Test
    public void testStopGoals_OtherUsersInGoal() {
        firstGoal.getUsers().add(new User());
        user.setGoals(List.of(firstGoal));
        userService.stopGoals(user);
        verify(goalRepository, never()).deleteById(anyLong());
        assertTrue(user.getGoals().isEmpty());
    }

    @Test
    public void testStopEvents() {
        user.setOwnedEvents(List.of(firstEvent));
        user.setParticipatedEvents(List.of(secondEvent));
        userService.stopEvents(user);
        assertTrue(user.getOwnedEvents().isEmpty());
        assertTrue(user.getParticipatedEvents().isEmpty());
        verify(eventRepository, times(1)).deleteAllById(any());
    }
}