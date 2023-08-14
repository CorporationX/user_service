package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeactivatingServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private GoalRepository goalRepository;

    @Mock
    private MentorshipService mentorshipService;
    @InjectMocks
    private UserService service;

    @Test
    void deactivatingTheUser() {
        User user = User.builder().id(23L).username("Sveta").active(true).build();
        User user2 = User.builder().id(32L).username("Pavel").active(false).build();

        List<User> users = List.of(user);
        List<User> usersFalse = List.of(user2, user);

        Goal goal1 = Goal.builder().id(1L).mentor(user).users(users).build();
        Goal goal2 = Goal.builder().id(2L).mentor(user2).users(usersFalse).build();

        Stream<Goal> goalStream = Stream.of(goal1, goal2);

        when(goalRepository.findGoalsByUserId(23L)).thenReturn(goalStream);
        when(userRepository.findById(23L)).thenReturn(Optional.of(user));

        List<Event> checkTrue = List.of(Event.builder().owner(user).build());
        when(eventRepository.findAllByUserId(23L)).thenReturn(List.of(Event.builder().owner(user).build()
                , Event.builder().owner(user2).build()));

        service.deactivateUser(user.getId());

        verify(eventRepository).deleteAll(checkTrue);

        verify(goalRepository).findGoalsByUserId(23L);

    }
}