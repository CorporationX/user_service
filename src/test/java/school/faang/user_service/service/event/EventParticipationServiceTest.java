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

    private List<User> participants;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @BeforeEach
    public void setUp() {
        this.user = new User();
        this.event = new Event();
        this.participants = List.of(
            new User(),
            new User(),
            new User(),
            new User()
        );
    }

    @Test
    @Description("успешная регистрация юзера на мероприятие")
    void test_register_participant_should_success_register () {
        long eventId = event.getId();
        long userId = user.getId();
        eventParticipationService.registerParticipant(eventId, userId);
        Mockito.verify(eventParticipationRepository, Mockito.times(1)).register(eventId, userId);
    }

    @Test
    @Description("успешная регистрация одного и того же юзера на два разных мероприятия")
    void test_register_participant_should_success_register_for_other_event() {
        long userId = user.getId();
        long eventId = event.getId();
        eventParticipationService.registerParticipant(eventId, userId);

        long otherEventId = 2L;
        eventParticipationService.registerParticipant(otherEventId, userId);
        Mockito.verify(eventParticipationRepository, Mockito.times(1)).register(otherEventId, userId);
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

    @Test
    @Description("успешная отмена регистрации юзера на мероприятие")
    void test_unregister_participant_should_success() {
        long eventId = event.getId();
        long userId = user.getId();

        eventParticipationService.registerParticipant(event.getId(), user.getId());
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(event.getId())).thenReturn(List.of(user));

        eventParticipationService.unregisterParticipant(eventId, userId);
        Mockito.verify(eventParticipationRepository, Mockito.times(1)).unregister(eventId, userId);
    }

    @Test
    @Description("исключение выброшено, если пользователь не зарегистрирован")
    void test_unregister_participant_should_throw_exception() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(event.getId(), user.getId()));
    }

    @Test
    @Description("успешное получение списка участников мероприятия, если пользователи зарегистрированы на мероприятие")
    void test_get_participants_should_return_list(){
        long eventId = event.getId();
        long userId = user.getId();

        eventParticipationService.registerParticipant(eventId, userId);

        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(event.getId())).thenReturn(List.of(user));
        List<User> participants = eventParticipationService.getParticipants(eventId);

        Assertions.assertNotNull(participants);
        Assertions.assertEquals(1, participants.size());
    }

    @Test
    @Description("получение пустого списка участников мероприятия, если нет участников на мероприятие")
    void test_get_participants_should_return_empty(){
        Assertions.assertEquals(List.of(), eventParticipationService.getParticipants(event.getId()));
    }

    @Test
    @Description("получение количества участников события, равного 1")
    void test_get_participants_count_should_return_one(){
        long eventId = event.getId();
        long userId = user.getId();

        eventParticipationService.registerParticipant(eventId, userId);

        Mockito.when(eventParticipationRepository.countParticipants(event.getId())).thenReturn(1);
        int participantCount = eventParticipationService.getParticipantsCount(eventId);

        Assertions.assertNotEquals(0, participantCount);
        Assertions.assertEquals(1, participantCount);
    }

    @Test
    @Description("получение количества участников события, равного 4")
    void test_get_participants_count_should_return_number_greater_than_one(){
        long eventId = event.getId();
        for (User participant : participants) {
            eventParticipationService.registerParticipant(eventId, participant.getId());
        }


        Mockito.when(eventParticipationRepository.countParticipants(event.getId())).thenReturn(4);
        int participantCount = eventParticipationService.getParticipantsCount(eventId);

        Assertions.assertNotEquals(0, participantCount);
        Assertions.assertEquals(4, participantCount);
    }

    @Test
    @Description("получение количества участников события, равного 0")
    void test_get_participants_count_should_return_zero(){
        Assertions.assertEquals(0, eventParticipationService.getParticipantsCount(event.getId()));
    }
}