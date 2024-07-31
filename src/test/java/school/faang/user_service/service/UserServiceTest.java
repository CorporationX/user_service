package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserMapper userMapper;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private MentorshipService mentorshipService;
  
    private User user;
    private User mentee;
    private Goal mentorAssignedGoal;

    @BeforeEach
    public void setUp() {
        List<Goal> goalList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        List<User> menteesList = new ArrayList<>();
        List<Event> ownedEvents = new ArrayList<>();
        user = User.builder()
                .id(1L)
                .goals(goalList)
                .ownedEvents(ownedEvents)
                .mentees(menteesList).build();
        Goal goal = Goal.builder()
                .id(1L)
                .users(userList).build();
        mentorAssignedGoal = Goal.builder()
                .mentor(user).build();
        mentee = User.builder()
                .id(2L)
                .goals(List.of(mentorAssignedGoal)).build();
        Event event = Event.builder().id(1L)
                .status(EventStatus.PLANNED).build();
        menteesList.add(mentee);
        goalList.add(goal);
        userList.add(user);
        ownedEvents.add(event);
    }

    @Test
    @DisplayName("testing deactivateUser by providing non existing user id")
    public void testDeactivateUserWithNonExistingUserId() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.deactivateUser(userId));
    }

    @Test
    @DisplayName("testing deactivateUser by goalRepository deleteAll method execution")
    public void testDeactivateUserWithRepositoryGoalDeletion() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.deactivateUser(user.getId());
        verify(goalRepository, times(1)).deleteAll(any());
    }

    @Test
    @DisplayName("testing deactivateUser by eventRepository saveAll method execution")
    public void testDeactivateUserWithEventRepositorySaveExecution() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.deactivateUser(user.getId());
        verify(eventRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("testing deactivateUser by deleteMentor method execution")
    public void testDeactivateUserWithDeleteMethodExecution() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.deactivateUser(user.getId());
        verify(mentorshipService, times(1)).deleteMentor(mentee.getId(), user.getId());
        assertEquals(mentorAssignedGoal.getMentor(), mentee);
    }
  
    @Test
    @DisplayName("invoke findAll method and mapper method")
    public void testToInvokeRepositoryAndMapperMethodsWhenGetUsersDtoByIds() {
        List<Long> ids = new ArrayList<>(List.of(1L, 2L));

        when(userRepository.findAllById(anyList())).thenReturn(new ArrayList<>());
        userService.getUsersDtoByIds(ids);

        verify(userRepository, times(1)).findAllById(anyList());
        verify(userMapper, times(1)).usersToUserDTOs(anyList());
    }
}