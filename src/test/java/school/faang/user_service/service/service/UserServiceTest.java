package school.faang.user_service.service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.User_Service;

import java.util.ArrayList;
import java.util.List;
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

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private User_Service user_service;

    private User user;
    private User mentee;
    private Goal goal;
    private Goal goalForMentee;
    private Event event;

    @BeforeEach
    public void setUp() {
        user = User.builder().id(1L).active(true).build();
        mentee = User.builder().id(2L).active(true).build();
        List<User> userMentees = new ArrayList<>();
        userMentees.add(mentee);
        List<User> userList = new ArrayList<>();
        userList.add(user);
        goal = Goal.builder().id(2L).users(userList).build();
        goalForMentee = Goal.builder().id(3L).mentor(user).build();
        mentee.setGoals(List.of(goalForMentee));
        List<Goal> userGoals = new ArrayList<>();
        userGoals.add(goal);
        user.setGoals(userGoals);
        event = Event.builder().id(3L).status(EventStatus.PLANNED).build();
        List<Event> events = new ArrayList<>();
        events.add(event);
        user.setOwnedEvents(events);
    }

    @Test
    public void testDeactivate() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when((eventRepository).save(Mockito.any(Event.class))).thenReturn(null);
        Mockito.doNothing().when(goalRepository).delete(Mockito.any(Goal.class));
        user_service.deactivate(user.getId());
        Mockito.verify(userRepository, Mockito.times(3)).findById(user.getId());
        Mockito.verify(goalRepository, Mockito.times(1)).delete(Mockito.any(Goal.class));
        Mockito.verify(eventRepository, Mockito.times(1)).save(Mockito.any(Event.class));
        Assertions.assertFalse(user.isActive());
        Assertions.assertEquals(EventStatus.CANCELED, event.getStatus());
    }

}
