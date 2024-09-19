package school.faang.user_service.service.event;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.EventParticipationValidator.EventParticipationValidator;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {

    private static final long ID = 1L;
    private static final long EVENT_ID_IS_ONE = 1L;
    private static final long EVENT_ID_IS_TWO = 2L;
    private static final long USER_ID_IS_ONE = 1L;
    private static final long USER_ID_IS_TWO = 2L;

    private static final int ONE_FINAL_EVENTS = 1;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private EventParticipationValidator eventParticipationValidator;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Check that one event was deleted and one remains")
        void whenListParticipantsSizeIsTwoThenSuccess() {
            Event event = Event.builder()
                    .id(EVENT_ID_IS_ONE)
                    .build();
            Event event1 = Event.builder()
                    .id(EVENT_ID_IS_TWO)
                    .build();
            ArrayList<Event> events = new ArrayList<>();
            events.add(event);
            events.add(event1);
            User user1 = User.builder()
                    .id(USER_ID_IS_ONE)
                    .participatedEvents(events)
                    .build();
            User user2 = User.builder()
                    .id(USER_ID_IS_TWO)
                    .participatedEvents(events)
                    .build();
            List<User> participants = List.of(user1, user2);
            event.setAttendees(participants);

            eventParticipationService.deleteParticipantsFromEvent(event);

            assertEquals(ONE_FINAL_EVENTS, user1.getParticipatedEvents().size());
            assertEquals(ONE_FINAL_EVENTS, user2.getParticipatedEvents().size());
        }

        @Test
        @DisplayName("Ошибка при регистрации если пользователь уже зарегистрирован")
        void whenRegisterParticipantIfUserExistsThenException() {
            doThrow(new ValidationException("Пользователь уже зарегистрирован")).
                    when(eventParticipationValidator).validateUserRegister(ID);

            assertThrows(ValidationException.class,
                    () -> eventParticipationService.registerParticipant(ID, ID));
        }

        @Test
        @DisplayName("Ошибка при отмене регистрации если пользователь уже зарегистрирован")
        void whenUnregisterParticipantIfUserNoExistsThenException() {
            doThrow(new ValidationException("Пользователь ещё не зарегистрирован")).
                    when(eventParticipationValidator).validateUserUnregister(ID);

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

            verify(eventParticipationValidator).validateUserRegister(ID);
            verify(eventParticipationRepository).register(ID, ID);
        }

        @Test
        @DisplayName("Успешная отмена регистрации")
        void whenUnregisterParticipantThenSuccess() {
            eventParticipationService.unregisterParticipant(ID, ID);

            verify(eventParticipationValidator).validateUserUnregister(ID);
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

            verify(eventParticipationRepository).findAllParticipantsByEventId(ID);
            verify(userMapper).toDtos(users);
        }

        @Test
        @DisplayName("Успешное получение количества участников события")
        void whenGetParticipantCountThenSuccess() {
            eventParticipationService.getParticipantCount(ID);

            verify(eventParticipationRepository).countParticipants(ID);
        }
    }
}