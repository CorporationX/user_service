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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {
    @InjectMocks
    private EventController eventController;
    @Mock
    private EventService eventService;
    private EventDto eventDto = new EventDto();

    @Test
    public void testCreateWithNullTitle() {
        eventDto.setTitle(null);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
        assertEquals("title can't be null or empty", exception.getMessage());
        verify(eventService, times(0)).create(eventDto);
    }

    @Test
    public void testCreateWithBlankTitle() {
        eventDto.setTitle("  ");

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
        assertEquals("title can't be null or empty", exception.getMessage());
        verify(eventService, times(0)).create(eventDto);
    }

    @Test
    public void testCreateWithNullStartDate() {
        eventDto.setTitle("event");
        eventDto.setStartDate(null);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
        assertEquals("getStartDate can't be null", exception.getMessage());
        verify(eventService, times(0)).create(eventDto);
    }

    @Test
    public void testCreateWithNullOwnerId() {
        eventDto.setTitle("event");
        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setOwnerId(0L);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
        assertEquals("ownerId can't be 0", exception.getMessage());
        verify(eventService, times(0)).create(eventDto);
    }

    @Test
    public void testCreateUsingEventService() {
        prepareForValidation(eventDto);
        when(eventService.create(eventDto)).thenReturn(new EventDto());

        eventController.create(eventDto);

        verify(eventService, times(1)).create(eventDto);
    }

    @Test
    public void testGetEvent() {
        long eventId = 1L;
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
        prepareForValidation(eventDto);

        eventController.updateEvent(eventDto);

        verify(eventService, times(1)).updateEvent(eventDto);
    }

    private void prepareForValidation(EventDto eventDto) {
        eventDto.setTitle("event");
        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setOwnerId(1L);
    }

}
