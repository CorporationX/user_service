package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.user.UserLifeCycleServiceImpl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserLifeCycleServiceImplTest {

    @Mock
    private MentorshipService mentorshipService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private UserLifeCycleServiceImpl userService;

    @Test
    void deactivateUser_WithValidId() {
        long id = 1L;
        boolean active = false;
        doNothing().when(goalRepository).deleteUnusedGoalsByMentorId(id);
        doNothing().when(eventRepository).deleteAllByOwnerId(id);
        doNothing().when(mentorshipService).stopMentorship(id);
        doNothing().when(userRepository).updateUserActive(id, active);

        userService.deactivateUser(id);

        verify(goalRepository).deleteUnusedGoalsByMentorId(id);
        verify(eventRepository).deleteAllByOwnerId(id);
        verify(mentorshipService).stopMentorship(id);
        verify(userRepository).updateUserActive(id, active);
    }

    @Test
    void deactivateUser_WithNull() {
        long id = 1L;
        boolean active = false;

        assertThrows(NullPointerException.class,
                () -> userService.deactivateUser(null));

        verify(goalRepository, never()).deleteUnusedGoalsByMentorId(id);
        verify(eventRepository, never()).deleteAllByOwnerId(id);
        verify(mentorshipService, never()).stopMentorship(id);
        verify(userRepository, never()).updateUserActive(id, active);
    }
}