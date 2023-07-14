package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {
    EventDto eventDto;
    @Mock
    private EventService eventService;
    @InjectMocks
    private EventController eventController;

    @BeforeEach
    public void init() {
        eventDto = new EventDto(4L, "fdgdfg", LocalDateTime.now(), LocalDateTime.now(),
                0L, "hfgh", new ArrayList<>(), "location", 1);
    }

    @Test
    public void testThrowDataValidationException() {
        eventDto.setTitle(null);
        assertThrows(DataValidationException.class, () -> {
            eventController.createEvent(eventDto);
        });
    }

    @Test
    public void testCreateEvent() {
        eventController.createEvent(eventDto);
        Mockito.verify(eventService, Mockito.times(1)).createEvent(eventDto);
    }

    @Test
    public void testUpdateEvent() {
        eventController.updateEvent(eventDto);
        Mockito.verify(eventService, Mockito.times(1)).updateEvent(eventDto);
    }

    @Test
    public void testUpdateEventThrowsDataValidationException() {
        assertThrows(DataValidationException.class, () -> {
            eventController.updateEvent(null);
        });
        eventDto.setTitle(null);
        assertThrows(DataValidationException.class, () -> {
            eventController.updateEvent(eventDto);
        });
    }
}
