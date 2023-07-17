package school.faang.user_service.service;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
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
    public void registerParticipantThrowsException() {
        User user = User.builder().id(1L).username("test").build();
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of(user));
        assertThrows(DataValidationException.class, () -> {
            eventParticipationService.registerParticipant(1L, 1L);
        });
    }

    @Test
    public void unregisterParticipantTest() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(Collections.emptyList());
        eventParticipationService.unregisterParticipant(1L, 10L);
        Mockito.verify(eventParticipationRepository).findAllParticipantsByEventId(1L);
        Mockito.verify(eventParticipationRepository).unregister(1L, 10L);
    }

    @Test
    public void unregisterParticipantThrowExceptionTest() {
        assertThrows(DataValidationException.class,
                () -> eventParticipationService.unregisterParticipant(1L, 10L));
    }

    @Test
    public void getCountRegisteredParticipantTest() {
        Mockito.when(eventParticipationRepository.existsById(1L)).thenReturn(true);
        Mockito.when(eventParticipationRepository.countParticipants(1L)).thenReturn(1);
        Assertions.assertEquals(1, eventParticipationService.getCountRegisteredParticipant(1L));
        Mockito.verify(eventParticipationRepository).countParticipants(1L);
        Mockito.verify(eventParticipationService).validateEventId(1L);
    }

    @Test
    public void getParticipantsCountThrowsException() {
        Mockito.when(eventParticipationRepository.existsById(1L)).thenReturn(false);
        assertThrows(DataValidationException.class,
                () -> eventParticipationService.getCountRegisteredParticipant(1L));
    }
}
