package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.validator.user.UserValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDeactivationServiceTest {

    private static final long USER_ID_IS_ONE = 1L;

    private static final int USER_MENTEES_SIZE_IS_ZERO = 0;
    private static final int USER_MENTEES_SIZE_IS_ONE = 1;

    @InjectMocks
    private UserDeactivationService userDeactivationService;

    @Mock
    private UserService userService;

    @Mock
    private GoalService goalService;

    @Mock
    private EventService eventService;

    @Mock
    private MentorshipService mentorshipService;

    @Mock
    private UserValidator userValidator;

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Throws exception when user not found")
        void whenUserNotFoundThenThrowEntityNotFoundException() {
            when(userService.getUserById(anyLong()))
                    .thenThrow(new EntityNotFoundException());

            assertThrows(EntityNotFoundException.class,
                    () -> userDeactivationService.deactivateAccount(anyLong()));
        }
    }

    @Nested
    class PositiveTests {

        private User user;

        @BeforeEach
        void init() {
            List<User> mentees = new ArrayList<>();
            mentees.add(new User());

            user = User.builder()
                    .id(USER_ID_IS_ONE)
                    .goals(List.of(new Goal()))
                    .active(Boolean.TRUE)
                    .mentees(mentees)
                    .build();
        }

        @Test
        @DisplayName("Deactivate account and drop all goals and mentees if userId is correct and found in DB")
        void whenUserIdCorrectThenDeactivateProfileAndRemoveMentees() {
            when(userService.getUserById(USER_ID_IS_ONE))
                    .thenReturn(user);

            assertEquals(USER_MENTEES_SIZE_IS_ONE, user.getMentees().size());

            userDeactivationService.deactivateAccount(USER_ID_IS_ONE);

            verify(userValidator)
                    .validateUserIdIsPositiveAndNotNull(USER_ID_IS_ONE);
            verify(userService)
                    .getUserById(USER_ID_IS_ONE);
            verify(goalService)
                    .deactivateActiveUserGoals(user);
            verify(eventService)
                    .deactivatePlanningUserEventsAndDeleteEvent(user);
            verify(mentorshipService)
                    .removeUserFromListHisMentees(user);

            assertFalse(user.isActive());
            assertEquals(USER_MENTEES_SIZE_IS_ZERO, user.getMentees().size());
        }
    }
}