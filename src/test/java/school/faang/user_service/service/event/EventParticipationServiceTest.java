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
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.user.UserService;

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
    private UserService userService;

    @Mock
    private EventService eventService;

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
        Mockito.when(userService.existsById(userId)).thenReturn(true);
        Mockito.when(eventService.existsById(eventId)).thenReturn(true);
        eventParticipationService.registerParticipant(eventId, userId);
        Mockito.verify(eventParticipationRepository, Mockito.times(1)).register(eventId, userId);
    }


    @Test
    void test_register_participant_should_success_register_for_other_event() {
        long eventId = event.getId();
        long userId = user.getId();
        Mockito.when(userService.existsById(userId)).thenReturn(true);
        Mockito.when(eventService.existsById(eventId)).thenReturn(true);
        eventParticipationService.registerParticipant(eventId, userId);

        Event otherEvent = new Event();
        long otherEventId = otherEvent.getId();
        Mockito.when(userService.existsById(userId)).thenReturn(true);
        Mockito.when(eventService.existsById(eventId)).thenReturn(true);
        eventParticipationService.registerParticipant(otherEventId, userId);
        Mockito.verify(eventParticipationRepository, Mockito.times(2)).register(eventId, userId);
    }

    @Test
    void test_register_participant_should_throw_exception_if_user_already_registered() {
        long eventId = event.getId();
        long userId = user.getId();
        Mockito.when(userService.existsById(userId)).thenReturn(true);
        Mockito.when(eventService.existsById(eventId)).thenReturn(true);
        eventParticipationService.registerParticipant(eventId, userId);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(event.getId())).thenReturn(List.of(user));
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(event.getId(), user.getId()));
    }

    @Test
    void test_register_participant_should_throw_exception_if_user_not_exist() {
        long eventId = event.getId();
        long userId = user.getId();
        Mockito.when(userService.existsById(userId)).thenThrow(IllegalArgumentException.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(eventId, userId));
    }

    @Test
    void test_register_participant_should_throw_exception_if_event_not_exist() {
        long eventId = event.getId();
        long userId = user.getId();
        Mockito.when(userService.existsById(userId)).thenReturn(true);
        Mockito.when(eventService.existsById(eventId)).thenThrow(IllegalArgumentException.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(eventId, userId));
    }

    @Test
    void test_get_participants_count_should_return_one(){
        long eventId = event.getId();
        long userId = user.getId();
        Mockito.when(userService.findById(userId)).thenReturn(Optional.ofNullable(user));
        Mockito.when(userService.findById(eventId)).thenReturn(Optional.ofNullable(event));

        eventParticipationService.registerParticipant(eventId, userId);
        Mockito.when(eventParticipationRepository.countParticipants(eventId)).thenReturn(1);
        int participantCount = eventParticipationService.getParticipantsCount(eventId);

        Assertions.assertNotEquals(0, participantCount);
        Assertions.assertEquals(1, participantCount);
    }

    @Test
    void test_get_participants_count_should_return_number_greater_than_one(){
        long eventId = event.getId();
        Mockito.when(userService.findById(eventId)).thenReturn(Optional.ofNullable(event));
        participants.forEach(participant -> {
            Mockito.when(userService.findById(participant.getId())).thenReturn(Optional.ofNullable(user));
            eventParticipationService.registerParticipant(eventId, participant.getId());
        });

        Mockito.when(eventParticipationRepository.countParticipants(event.getId())).thenReturn(4);
        int participantCount = eventParticipationService.getParticipantsCount(eventId);

        Assertions.assertNotEquals(0, participantCount);
        Assertions.assertEquals(4, participantCount);
    }

    @Test
    void test_get_participants_count_should_return_zero(){
        long eventId = event.getId();
        Mockito.when(userService.findById(eventId)).thenReturn(Optional.ofNullable(event));
        Assertions.assertEquals(0, eventParticipationService.getParticipantsCount(eventId));
    }
}