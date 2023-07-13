package school.faang.user_service.service.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;
    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    public void registerParticipantThrowsException() {
        User user = User.builder().id(1L).username("test").build();
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of(user));
        Assertions.assertThrows(DataValidationException.class, () -> {
            eventParticipationService.registerParticipant(1L, 1L);
        });
    }

    @Test
    public void registerParticipant() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of());
        eventParticipationService.registerParticipant(1L, 1L);
        Mockito.verify(eventParticipationRepository).register(1L, 1L);
    }

    @Test
    public void unregisterParticipantThrowsException() {
        User user = User.builder().id(1L).username("test").build();
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of(user));
        Assertions.assertThrows(DataValidationException.class, () -> {
            eventParticipationService.unregisterParticipant(1L, 2L);
        });
    }

    @Test
    public void unregisterParticipant() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of());
        eventParticipationService.unregisterParticipant(1L, 1L);
        Mockito.verify(eventParticipationRepository).unregister(1L, 1L);
    }
}