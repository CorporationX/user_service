package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.event.EventParticipationService;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {
    private static final long EVENT_ID = 1L;
    private static final long USER_ID = 10L;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    public void testExceptionIfUserIsAlreadyRegisteredToEvent() {
        User user = new User();
        user.setId(USER_ID);
        List<User> users = List.of(user);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(users);
        assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.registerParticipant(EVENT_ID, USER_ID));
        Mockito.verify(eventParticipationRepository, Mockito.times(0))
                .register(EVENT_ID, USER_ID);
    }

    @Test
    public void testUserIsSuccessfullyRegisteredToEvent() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(Collections.emptyList());
        eventParticipationService.registerParticipant(EVENT_ID, USER_ID);
        Mockito.verify(eventParticipationRepository, Mockito.times(1))
                .register(EVENT_ID, USER_ID);
    }

    @Test
    public void testUnregisterUserFromEvent() {
        User user = new User();
        user.setId(USER_ID);
        List<User> users = List.of(user);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(users);
        eventParticipationService.unregisterParticipant(EVENT_ID, USER_ID);
        Mockito.verify(eventParticipationRepository, Mockito.times(1))
                .unregister(EVENT_ID, USER_ID);
    }

    @Test
    public void testExceptionIfUserIsNotRegisteredToEvent() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID))
                .thenReturn(Collections.emptyList());
        assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.unregisterParticipant(EVENT_ID, USER_ID));
        Mockito.verify(eventParticipationRepository, Mockito.times(0))
                .unregister(EVENT_ID, USER_ID);
    }

    @Test
    public void testGetUserOfEvent() {
        eventParticipationService.getParticipant(EVENT_ID);
        Mockito.verify(eventParticipationRepository, Mockito.times(1))
                .findAllParticipantsByEventId(EVENT_ID);
    }

    @Test
    public void testGetUserCount() {
        eventParticipationService.getParticipantsCount(EVENT_ID);
        Mockito.verify(eventParticipationRepository, Mockito.times(1))
                .countParticipants(EVENT_ID);
    }
}