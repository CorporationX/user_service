package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final long USER_ID = 1L;
    private static final long SET_GOAL_ID = 1L;
    private static final long GOAL_ID = 2L;
    private static final long EVENT_ID = 1L;

    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private MentorshipRepository mentorshipRepository;

    @InjectMocks
    private UserService userService;

    User user;

    @BeforeEach
    public void init() {
        user = User.builder()
                .id(USER_ID)
                .build();
    }

    @Test
    public void deactivateWhenUserNotExists() {
        String errMessage = String.format("User doesn't exist by id: %s", USER_ID);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.deactivate(USER_ID));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    public void deactivateUser() {
        Goal setGoal = Goal.builder()
                .id(SET_GOAL_ID)
                .build();
        Goal goal = Goal.builder()
                .id(GOAL_ID)
                .users(List.of(user))
                .build();
        Event userEvent = Event.builder()
                .id(EVENT_ID)
                .owner(user)
                .build();
        user.setGoals(List.of(goal));
        user.setSetGoals(List.of(setGoal));
        user.setParticipatedEvents(List.of(userEvent));

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.deactivate(USER_ID);

        verify(mentorshipRepository).deleteAllMentorshipByMentorId(USER_ID);
        verify(goalRepository).save(setGoal);
        verify(goalRepository).deleteById(GOAL_ID);
        verify(eventRepository).deleteById(EVENT_ID);
    }
}