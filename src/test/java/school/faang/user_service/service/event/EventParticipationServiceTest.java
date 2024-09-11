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
                    when(userValidator).userIdIsNotNullOrElseThrowValidationException(1L);
            assertThrows(ValidationException.class,
                    () -> eventParticipationService.registerParticipant(1L, 1L));
        }

        @Test
        @DisplayName("Ошибка при отмене регистрации если пользователь уже зарегистрирован")
        void testUnregisterParticipantIfUserNoExists() {
            doThrow(new ValidationException("Пользователь ещё не зарегистрирован")).
                    when(userValidator).userIdIsNotNullOrElseThrowValidationException(1L);
            assertThrows(ValidationException.class,
                    () -> eventParticipationService.unregisterParticipant(1L, 1L));
        }
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Успешная регистрация")
        void testRegisterParticipant() {
            eventParticipationService.registerParticipant(1L, 1L);
            verify(userValidator).userIdIsNotNullOrElseThrowValidationException(1L);
            verify(eventValidator).checkIfRegisterParticipantThenThrowException(1L);
            verify(eventParticipationRepository).register(1L, 1L);
        }

        @Test
        @DisplayName("Успешная отмена регистрации")
        void testUnregisterParticipant() {
            eventParticipationService.unregisterParticipant(1L, 1L);
            verify(userValidator).userIdIsNotNullOrElseThrowValidationException(1L);
            verify(eventValidator).eventIdIsNotNullOrElseThrowValidationException(1L);
            verify(eventParticipationRepository).unregister(1L, 1L);
        }

        @Test
        @DisplayName("Успешное получение списка всех участников события")
        void testGetParticipant() {
            List<User> users = List.of(User.builder()
                    .id(1L)
                    .build());
            when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(users);
            eventParticipationService.getParticipant(1L);
            verify(eventValidator).checkIfUnregisterParticipantThenThrowException(1L);
            verify(eventParticipationRepository).findAllParticipantsByEventId(1L);
            verify(userMapper).toDtos(users);
        }

        @Test
        @DisplayName("Успешное получение количетва участников события")
        void testGetParticipantCount() {
            eventParticipationService.getParticipantCount(1L);
            verify(eventValidator).eventIdIsNotNullOrElseThrowValidationException(1L);
            verify(eventParticipationRepository).countParticipants(1L);
        }
    }
}
