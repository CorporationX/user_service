package school.faang.user_service.service;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    public void registerParticipantTest() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(Collections.emptyList());
        eventParticipationService.registerParticipant(1L, 10L);
        Mockito.verify(eventParticipationRepository).register(1L, 10L);
    }

    @Test
    public void registerParticipantThrowExceptionTest() {
        assertThrows(DataValidationException.class,
                () -> eventParticipationService.registerParticipant(1L, 10L));
    }

    @Test
    public void unregisterParticipantTest() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(Collections.emptyList());
        eventParticipationService.unregisterParticipant(1L, 10L);
        Mockito.verify(eventParticipationRepository).unregister(1L, 5L);
    }

    @Test
    public void unregisterParticipantThrowExceptionTest() {
        assertThrows(DataValidationException.class,
                () -> eventParticipationService.registerParticipant(1L, 10L));
    }
}