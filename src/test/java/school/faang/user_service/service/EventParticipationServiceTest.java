package school.faang.user_service.service;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;


public class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    public void registerParticipantTest() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of());
        eventParticipationService.registerParticipant(1L, 10L);
        Mockito.verify(eventParticipationRepository).register(1L, 10L);
    }

    @Test
    public void unregisterParticipantTest() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of());
        eventParticipationService.unregisterParticipant(1L, 10L);
        Mockito.verify(eventParticipationRepository).unregister(1L, 10L);
    }

    @Test
    public void getCountRegisteredParticipantTest() {
        Mockito.when(eventParticipationRepository.existsById(1L)).thenReturn(true);
        Mockito.when(eventParticipationRepository.countParticipants(1L)).thenReturn(1);
        Assertions.assertEquals(1, eventParticipationService.getCountRegisteredParticipant(1L));
    }

    @Test
    public void getParticipantsCountThrowsException() {
        Mockito.when(eventParticipationRepository.existsById(1L)).thenReturn(false);
        Assertions.assertThrows(DataValidationException.class, () -> {
            eventParticipationService.getCountRegisteredParticipant(1L);
        });
    }
}
