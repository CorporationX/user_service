package school.faang.user_service.service;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.event.EventParticipationService;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    public void getParticipantTest() {
        Mockito.when(eventParticipationRepository.existsById(1L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class,
                () -> eventParticipationService.getParticipant(1L));
    }
}
