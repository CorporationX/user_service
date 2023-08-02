package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.event.EventParticipationController;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventParticipationService;

import static org.junit.jupiter.api.Assertions.assertThrows;
@ExtendWith(MockitoExtension.class)
public class EventParticipationControllerTest {
    @Mock
    private EventParticipationService eventParticipationService;

    @InjectMocks
    private EventParticipationController eventParticipationController;

    @Test
    public void validateThrowExceptionTest() {
        assertThrows(DataValidationException.class, () -> {
            eventParticipationController.validate(-1L, 1L);
        });
        assertThrows(DataValidationException.class, () -> {
            eventParticipationController.validate(null, 1L);
        });
    }
}

    public void checkValidateThrowsExceptionTest() {
        assertThrows(DataValidationException.class,
                () -> eventParticipationController.registerParticipantController(null, null));
        assertThrows(DataValidationException.class,
                () -> eventParticipationController.registerParticipantController(-1L, 1L));
    }
}