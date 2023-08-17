package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventParticipationService;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class EventParticipationControllerTest {
    @Mock
    private EventParticipationService eventParticipationService;
    @InjectMocks
    private EventParticipationController eventParticipationController;

    @Test
    public void registerParticipantThrowsException() {
        assertThrows(DataValidationException.class, () -> {
            eventParticipationController.registerParticipant(-1L, 1L);
        });
        assertThrows(DataValidationException.class, () -> {
            eventParticipationController.registerParticipant(null, 1L);
        });
    }
}