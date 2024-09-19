package school.faang.user_service.service.event;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.event.EventValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {

    private static final long ID = 1L;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserValidator userValidator;

    @Mock
    private EventValidator eventValidator;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Ошибка при регистрации если пользователь уже зарегистрирован")
        void testRegisterParticipantIfUserExists() {
            doThrow(new ValidationException("Пользователь уже зарегистрирован")).
                    when(userValidator).validateUserId(ID);

            assertThrows(ValidationException.class,
                    () -> eventParticipationService.registerParticipant(ID, ID));
        }

        @Test
        @DisplayName("Ошибка при отмене регистрации если пользователь уже зарегистрирован")
        void testUnregisterParticipantIfUserNoExists() {
            doThrow(new ValidationException("Пользователь ещё не зарегистрирован")).
                    when(userValidator).validateUserId(ID);

            assertThrows(ValidationException.class,
                    () -> eventParticipationService.unregisterParticipant(ID, ID));
        }
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Успешная регистрация")
        void whenRegisterParticipantThenSuccess() {
            eventParticipationService.registerParticipant(ID, ID);

            verify(userValidator).validateUserId(ID);
            verify(userValidator).validateUserRegister(ID);
            verify(eventParticipationRepository).register(ID, ID);
        }

        @Test
        @DisplayName("Успешная отмена регистрации")
        void testUnregisterParticipantThenSuccess() {
            eventParticipationService.unregisterParticipant(ID, ID);

            verify(userValidator).validateUserId(ID);
            verify(eventValidator).validateEventId(ID);
            verify(userValidator).validateUserUnregister(ID);
            verify(eventParticipationRepository).unregister(ID, ID);
        }

        @Test
        @DisplayName("Успешное получение списка всех участников события")
        void whenGetParticipantThenSuccess() {
            List<User> users = List.of(User.builder()
                    .id(ID)
                    .build());

            when(eventParticipationRepository.findAllParticipantsByEventId(ID))
                    .thenReturn(users);

            eventParticipationService.getParticipant(ID);

            verify(eventValidator).validateEventId(ID);
            verify(eventParticipationRepository).findAllParticipantsByEventId(ID);
            verify(userMapper).toDtos(users);
        }

        @Test
        @DisplayName("Успешное получение количества участников события")
        void testGetParticipantCount() {
            eventParticipationService.getParticipantCount(ID);

            verify(eventValidator).validateEventId(ID);
            verify(eventParticipationRepository).countParticipants(ID);
        }
    }
}
