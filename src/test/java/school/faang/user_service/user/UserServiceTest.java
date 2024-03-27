package school.faang.user_service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;
    private UserMapper userMapper;

    private MentorshipService mentorshipService;
    private EventRepository eventRepository;
    private GoalService goalService;

    private User user;
    private User mentee;
    private Goal goal;
    private Event event;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .active(true)
                .build();
        mentee = User.builder()
                .id(2L)
                .mentors(new ArrayList<>(List.of(user)))
                .build();
        goal = Goal.builder()
                .id(3L)
                .mentor(user)
                .status(GoalStatus.ACTIVE)
                .users(new ArrayList<>(List.of(user)))
                .build();
        event = Event.builder()
                .id(4L)
                .owner(user)
                .status(EventStatus.PLANNED)
                .build();
        user.setMentees(new ArrayList<>(List.of(mentee)));
        user.setGoals(new ArrayList<>(List.of(goal)));
        user.setOwnedEvents(new ArrayList<>(List.of(event)));

        userRepository = mock(UserRepository.class);
        mentorshipService = mock(MentorshipService.class);
        eventRepository = mock(EventRepository.class);
        goalService = mock(GoalService.class);
        userMapper = mock(UserMapper.class);
        userService = new UserService(userRepository, userMapper, mentorshipService, goalService,eventRepository);
    }

    @Test
    void deactivateUser_UserIsDeactivatedAndSavedToDb_GoalsAndEventsAlsoDeleted() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.deactivationUserById(user.getId());


        verify(mentorshipService, times(1)).deleteMentorForHisMentees(user.getId(), List.of(mentee));
        verify(goalService, times(1)).deleteGoal(goal.getId());
        verify(eventRepository, times(1)).deleteById(event.getId());
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(user);
        assertFalse(user.isActive());
        assertEquals(Collections.emptyList(), goal.getUsers());
        assertEquals(Collections.emptyList(), user.getMentees());
        assertEquals(Collections.emptyList(), user.getGoals());
        assertEquals(Collections.emptyList(), user.getOwnedEvents());
    }
}