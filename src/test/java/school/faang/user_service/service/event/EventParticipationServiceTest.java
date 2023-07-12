package school.faang.user_service.service.event;

import jdk.jfr.Description;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {
    private User user;

    private Event event;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @BeforeEach
    public void setUp() {
        this.user = new User();
        this.event = new Event();
    }

    @Test
    @Description("успешная регистрация юзера на мероприятие")
    void test_register_participant_should_success_register () {
        long eventId = 1L;
        long userId = 111L;
        eventParticipationService.registerParticipant(eventId, userId);
        Mockito.verify(eventParticipationRepository, Mockito.times(1)).register(eventId, userId);
    }

    @Test
    @Description("успешная регистрация одного и того же юзера на два разных мероприятия")
    void test_register_participant_should_success_register_for_other_event() {
        long userId = 111L;

        long eventId = 1L;
        eventParticipationService.registerParticipant(eventId, userId);

        long otherEventId = 2L;
        eventParticipationService.registerParticipant(otherEventId, userId);
        Mockito.verify(eventParticipationRepository, Mockito.times(1)).register(eventId, userId);
    }

    @Test
    @Description("исключение выброшено, если пользователь зарегистрирован ранее")
    void test_register_participant_should_throw_exception() {
        // регистрируем участника на мероприятие
        eventParticipationService.registerParticipant(event.getId(), user.getId());
        // репозиторий возвращает список участников с заданным пользователем
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(event.getId())).thenReturn(List.of(user));
        // вызываем метод еще раз, чтобы проверить, что исключение выброшено, если пользователь зарегистрирован ранее
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(event.getId(), user.getId()));
    }
}