package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.user.UserMapper;
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
    @Mock
    private UserMapper userMapper;

    private static long USER_ID_IS_ONE = 1L;
    private static int USER_MENTEES_SIZE_IS_ZERO = 0;
    private static int USER_MENTEES_SIZE_IS_ONE = 1;
    private static final long USER_ID = 1L;
    private static final List<Long> USER_IDS = List.of(USER_ID);
    private static final String USER_NAME = "name";

    private User user;
    private User secondUser;
    private UserDto userDto;
    private List<User> users;
    private List<UserDto> userDtos;

    @BeforeEach
    public void init() {
        secondUser = User.builder()
                .id(USER_ID)
                .username(USER_NAME)
                .build();
        userDto = UserDto.builder()
                .id(USER_ID)
                .username(USER_NAME)
                .build();

        users = List.of(secondUser);
        userDtos = List.of(userDto);
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Успех если user существует")
        public void whenUserIsExistThenSuccess() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.ofNullable(secondUser));
            when(userMapper.toDto(secondUser)).thenReturn(userDto);

            UserDto resultUserDto = userService.getUser(USER_ID);

            assertNotNull(resultUserDto);
            verify(userValidator).validateUserIdIsPositiveAndNotNull(USER_ID);
            verify(userRepository).findById(USER_ID);
            verify(userMapper).toDto(secondUser);
        }

        @Test
        @DisplayName("Успех если users существуют")
        public void whenUsersIsExistThenSuccess() {
            when(userRepository.findAllById(USER_IDS)).thenReturn(users);
            when(userMapper.toDtos(users)).thenReturn(userDtos);

            List<UserDto> resultUserDros = userService.getUsersByIds(USER_IDS);

            assertNotNull(resultUserDros);
            verify(userValidator).validateUserIdIsPositiveAndNotNull(USER_ID);
            verify(userRepository).findAllById(USER_IDS);
            verify(userMapper).toDtos(users);
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Ошибка валидации если пользователя с переданным id не существует")
        void whenNullValueThenThrowValidationException() {
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> userService.deactivateAccount(anyLong()));
        }

        @Test
        @DisplayName("Ошибка если user не существует")
        public void whenUserIsNotExistThenThrowException() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            assertThrows(ValidationException.class, () -> userService.getUser(USER_ID));
        }

        @Test
        @DisplayName("Вернет пустой список users если users не существует")
        public void whenUsersIsNotExistThenReturnEmptyList() {
            when(userRepository.findAllById(USER_IDS)).thenReturn(List.of());
            when(userMapper.toDtos(List.of())).thenReturn(new ArrayList<>());

            List<UserDto> resultUserDros = userService.getUsersByIds(USER_IDS);

            assertEquals(0, resultUserDros.size());
            verify(userValidator).validateUserIdIsPositiveAndNotNull(USER_ID);
            verify(userRepository).findAllById(USER_IDS);
            verify(userMapper).toDtos(List.of());
        }
    }

    @Nested
    class DeactivateAccountMethod {

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
                    .validateUserIdIsPositiveAndNotNull(USER_ID_IS_ONE);
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