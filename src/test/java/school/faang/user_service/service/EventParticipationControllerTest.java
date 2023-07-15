package school.faang.user_service.service;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.controller.event.EventParticipationController;
import school.faang.user_service.service.event.EventParticipationService;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventParticipationControllerTest {
    @Mock
    private EventParticipationService eventParticipationService;

    @InjectMocks
    private EventParticipationController eventParticipationController;

    @Test
    public void validateTest() {
        assertThrows(IllegalArgumentException.class,
                () -> eventParticipationController.validate(1L, 10L));
    }

    @Test
    public void validateEventIdTest() {
        assertThrows(IllegalArgumentException.class,
                () -> eventParticipationController.validateEventID(1L));
    }
}