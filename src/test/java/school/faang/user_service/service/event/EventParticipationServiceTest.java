package school.faang.user_service.service.event;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.event.EventParticipationRepository;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    public void testRegisterParticipantSuccess() {
        long eventId = 1L;
        long userId = 100L;
        Mockito.when(eventParticipationRepository.existsUserByEventIdAndUserId(eventId, userId))
                .thenReturn(false);
        eventParticipationService.registerParticipant(eventId, userId);
        Mockito.verify(eventParticipationRepository).register(eventId, userId);
    }

    @Test
    public void testRegisterParticipantAlreadyRegistered() {
        long eventId = 1L;
        long userId = 100L;
        Mockito.when(eventParticipationRepository.existsUserByEventIdAndUserId(eventId, userId))
                .thenReturn(true);
        Assert.assertThrows(IllegalStateException.class, () ->
                eventParticipationService.registerParticipant(eventId, userId));
        Mockito.verify(eventParticipationRepository, Mockito.times(0))
                .register(eventId, userId);
    }

    @Test
    public void testUnregisterParticipantSuccess() {
        long eventId = 1L;
        long userId = 100L;
        Mockito.when(eventParticipationRepository.existsUserByEventIdAndUserId(eventId, userId))
                .thenReturn(true);
        eventParticipationService.unregisterParticipant(eventId, userId);
        Mockito.verify(eventParticipationRepository).unregister(eventId, userId);
    }

    @Test
    public void testUnregisterParticipantIsNotRegistered() {
        long eventId = 1L;
        long userId = 100L;
        Mockito.when(eventParticipationRepository.existsUserByEventIdAndUserId(eventId, userId))
                .thenReturn(false);
        Assert.assertThrows(IllegalStateException.class, () ->
                eventParticipationService.unregisterParticipant(eventId, userId));
        Mockito.verify(eventParticipationRepository, Mockito.times(0))
                .unregister(eventId, userId);
    }

    @Test
    void testGetParticipants() {
        long eventId = 1L;
        eventParticipationService.getParticipants(eventId);
        Mockito.verify(eventParticipationRepository,
                Mockito.times(1)).findAllParticipantsByEventId(eventId);
    }

    @Test
    public void testGetParticipantsCount() {
        long eventId = 1L;
        eventParticipationService.getParticipantsCount(eventId);
        Mockito.verify(eventParticipationRepository, Mockito.times(1))
                .countParticipants(eventId);
    }
}
