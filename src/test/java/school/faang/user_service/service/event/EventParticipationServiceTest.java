package school.faang.user_service.service.event;

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
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {
    private User user;

    private Event event;

    private List<User> participants;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

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
    void test_register_participant_should_success_register () {

        long eventId = event.getId();
        long userId = user.getId();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.ofNullable(event));
        eventParticipationService.registerParticipant(eventId, userId);
        Mockito.verify(eventParticipationRepository, Mockito.times(1)).register(eventId, userId);
    }


    @Test
    void test_register_participant_should_success_register_for_other_event() {
        long eventId = event.getId();
        long userId = user.getId();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.ofNullable(event));
        eventParticipationService.registerParticipant(eventId, userId);

        Event otherEvent = new Event();
        long otherEventId = otherEvent.getId();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.ofNullable(event));
        eventParticipationService.registerParticipant(otherEventId, userId);
        Mockito.verify(eventParticipationRepository, Mockito.times(2)).register(eventId, userId);
    }

    @Test
    void test_register_participant_should_throw_exception() {
        long eventId = event.getId();
        long userId = user.getId();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.ofNullable(event));
        eventParticipationService.registerParticipant(eventId, userId);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(event.getId())).thenReturn(List.of(user));
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(event.getId(), user.getId()));
    }

    @Test
    void test_unregister_participant_should_success() {
        long eventId = event.getId();
        long userId = user.getId();
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(event.getId())).thenReturn(List.of(user));

        eventParticipationService.unregisterParticipant(eventId, userId);
        Mockito.verify(eventParticipationRepository, Mockito.times(1)).unregister(eventId, userId);
    }

    @Test
    void test_unregister_participant_should_throw_exception() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(event.getId(), user.getId()));
    }

    @Test
    void test_get_participants_should_return_list(){
        long eventId = event.getId();
        long userId = user.getId();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.ofNullable(event));
        eventParticipationService.registerParticipant(eventId, userId);

        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of(user));
        List<User> participants = eventParticipationService.getParticipants(eventId);

        Assertions.assertNotNull(participants);
        Assertions.assertEquals(1, participants.size());
    }

    @Test
    void test_get_participants_should_return_empty(){
        Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.ofNullable(event));
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(event.getId())).thenReturn(List.of());
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