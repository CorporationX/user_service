package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventControllerTest {
    @InjectMocks
    private EventController eventController;
    @Mock
    private EventService eventService;

    @Test
    public void testCreateWithNullTitle() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle(null);

        assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
    }

    @Test
    public void testCreateWithBlankTitle() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("  ");

        assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
    }

    @Test
    public void testCreateWithNullStartDate() {
        EventDto eventDto = new EventDto();
        eventDto.setStartDate(null);

        assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
    }

    @Test
    public void testCreateWithNullOwnerId() {
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(0);

        assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
    }

}
