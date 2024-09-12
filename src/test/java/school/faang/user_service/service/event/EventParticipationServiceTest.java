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

    private final long ID = 1L;

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
                    when(userValidator).checkUserIdIsNotNull(ID);
            assertThrows(ValidationException.class,
                    () -> eventParticipationService.registerParticipant(ID, ID));
        }

        @Test
        @DisplayName("Ошибка при отмене регистрации если пользователь уже зарегистрирован")
        void testUnregisterParticipantIfUserNoExists() {
            doThrow(new ValidationException("Пользователь ещё не зарегистрирован")).
                    when(userValidator).checkUserIdIsNotNull(ID);
            assertThrows(ValidationException.class,
                    () -> eventParticipationService.unregisterParticipant(ID, ID));
        }
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Успешная регистрация")
        void testRegisterParticipant() {
            eventParticipationService.registerParticipant(ID, ID);

            verify(userValidator).checkUserIdIsNotNull(ID);
            verify(eventValidator).checkIfUserRegisterOnEvent(ID);
            verify(eventParticipationRepository).register(ID, ID);
        }

        @Test
        @DisplayName("Успешная отмена регистрации")
        void testUnregisterParticipant() {
            eventParticipationService.unregisterParticipant(ID, ID);

            verify(userValidator).checkUserIdIsNotNull(ID);
            verify(eventValidator).eventIdIsNotNullOrElseThrowValidationException(ID);
            verify(eventValidator).checkIfUserUnregisterOnEvent(ID);
            verify(eventParticipationRepository).unregister(ID, ID);
        }

        @Test
        @DisplayName("Успешное получение списка всех участников события")
        void testGetParticipant() {

            List<User> users = List.of(User.builder()
                    .id(ID)
                    .build());

            when(eventParticipationRepository.findAllParticipantsByEventId(ID))
                    .thenReturn(users);
            eventParticipationService.getParticipant(ID);

            verify(eventValidator).eventIdIsNotNullOrElseThrowValidationException(ID);
            verify(eventParticipationRepository).findAllParticipantsByEventId(ID);
            verify(userMapper).toDtos(users);
        }

        @Test
        @DisplayName("Успешное получение количетва участников события")
        void testGetParticipantCount() {
            eventParticipationService.getParticipantCount(ID);
            verify(eventValidator).eventIdIsNotNullOrElseThrowValidationException(ID);
            verify(eventParticipationRepository).countParticipants(ID);
        }
    }
}
