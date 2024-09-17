package school.faang.user_service.service.user;

import jakarta.validation.ValidationException;
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
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.validator.user.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserValidator userValidator;
    @Mock
    private GoalService goalService;
    @Mock
    private EventService eventService;
    @Mock
    private MentorshipService mentorshipService;

    private static long USER_ID_IS_ONE = 1L;
    private static int USER_MENTEES_SIZE_IS_ZERO = 0;
    private static int USER_MENTEES_SIZE_IS_ONE = 1;

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Ошибка валидации если пользователя с переданным id не существует")
        void whenNullValueThenThrowValidationException() {
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            assertThrows(ValidationException.class,
                    () -> userService.deactivateAccount(anyLong()));
        }
    }

    @Nested
    class DeactivateAccountMethod {

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
        @DisplayName("Если id пользователя прошел все проверки то деактивируем профиль и удаляем всех подопечных")
        void whenUserIdCorrectThenDeactivateProfileAndRemoveMentees() {
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(user));

            assertEquals(USER_MENTEES_SIZE_IS_ONE, user.getMentees().size());

            userService.deactivateAccount(USER_ID_IS_ONE);

            verify(userValidator)
                    .validateUser(USER_ID_IS_ONE);
            verify(userRepository)
                    .findById(USER_ID_IS_ONE);
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