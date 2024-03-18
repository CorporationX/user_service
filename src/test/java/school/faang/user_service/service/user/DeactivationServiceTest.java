package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.user.DeactivationService;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeactivationServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    GoalService goalService;
    @Mock
    EventService eventService;
    @Mock
    MentorshipService mentorshipService;
    @Mock
    UserMapper userMapper;
    @InjectMocks
    DeactivationService deactivationService;

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
    }

    @Test
    void deactivateUser_UserIsDeactivatedAndSavedToDb_GoalsAndEventsAlsoDeleted() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(user)).thenReturn(user);

        deactivationService.deactivateUser(user.getId());

        assertAll(
                () -> verify(mentorshipService, times(1)).
                        deleteMentorForAllHisMentees(user.getId(), List.of(mentee)),
                () -> verify(goalService, times(1)).deleteGoal(goal.getId()),
                () -> verify(eventService, times(1)).deleteEvent(event.getId()),
                () -> verify(userRepository, times(1)).save(user),
                () -> verify(userMapper, times(1)).toDto(user),
                () -> assertFalse(user.isActive()),
                () -> assertEquals(Collections.emptyList(), goal.getUsers()),
                () -> assertEquals(Collections.emptyList(), user.getMentees()),
                () -> assertEquals(Collections.emptyList(), user.getGoals()),
                () -> assertEquals(Collections.emptyList(), user.getOwnedEvents())
        );
    }
}
