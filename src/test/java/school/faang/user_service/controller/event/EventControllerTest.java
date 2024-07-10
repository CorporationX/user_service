package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {
    @Mock
    private EventService eventService;
    @Mock
    private EventDtoValidator validator;
    @InjectMocks
    private EventController eventController;
    private EventDto eventDto = new EventDto();
    private long eventId = 1L;


    @Test
    public void testCreateUsingWrongData() {
        String textException = "Wrong data";
        doThrow(new DataValidationException(textException))
                .when(validator).validate(eventDto);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
        assertEquals(textException, exception.getMessage());
        verify(eventService, times(0)).create(eventDto);
    }
    @Test
    public void testCreateUsingEventService() {
        eventController.create(eventDto);

        verify(validator, times(1)).validate(eventDto);
        verify(eventService, times(1)).create(eventDto);
    }

    @Test
    public void testGetEvent() {
        when(eventService.getEvent(eventId)).thenReturn(new EventDto());

        eventController.getEvent(eventId);

        verify(eventService, times(1)).getEvent(eventId);
    }

    @Test
    public void testGetEventsByFilter() {
        EventFilterDto filters = new EventFilterDto();
        when(eventService.getEventsByFilter(filters)).thenReturn(List.of(new EventDto()));

        List<EventDto> result = eventController.getEventsByFilter(filters);

        verify(eventService, times(1)).getEventsByFilter(filters);
        assertNotNull(result);
    }

    @Test
    public void testDeleteEvent() {
        long eventId = 1L;
        eventController.deleteEvent(eventId);

        verify(eventService, times(1)).deleteEvent(eventId);
    }

    @Test
    public void testUpdateEventAfterValidate() {
        eventController.updateEvent(eventDto);

        verify(validator, times(1)).validate(eventDto);
        verify(eventService, times(1)).updateEvent(eventDto);
    }

    @Test
    public void testGetOwnedEvents() {
        eventController.getOwnedEvents(eventId);

        verify(eventService, times(1)).getOwnedEvents(eventId);
    }

    @Test
    public void testGetParticipatedEvents() {
        eventController.getParticipatedEvents(eventId);

        verify(eventService, times(1)).getParticipatedEvents(eventId);
    }
}
