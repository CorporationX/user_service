package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.event.EventValidator;
import school.faang.user_service.service.event.EventService;


import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @Mock
    private EventService eventService;
    @Mock
    private EventValidator eventValidator;
    @InjectMocks
    private EventController eventController;

    @Test
    void shouldCreateEventController() {
        EventDto eventDto = new EventDto();
        eventController.create(eventDto);
        verify(eventService, times(1)).create(eventDto);
        verify(eventValidator, times(1)).validate(eventDto);
    }

    @Test
    void shouldGetEventEventController() {
        long eventId = 1;
        eventController.getEvent(eventId);
        verify(eventService, times(1)).getEvent(eventId);
    }

    @Test
    void shouldDeleteEventController() {
        long eventId = 1;
        eventController.deleteEvent(eventId);
        verify(eventService, times(1)).deleteEvent(eventId);
    }

    @Test
    void shouldUpdateEventController() {
        EventDto eventDto = new EventDto();
        eventController.updateEvent(eventDto);
        verify(eventService, times(1)).updateEvent(eventDto);
        verify(eventValidator, times(1)).validate(eventDto);
    }

    @Test
    void shouldGetOwnedEventsEventController() {
        EventFilterDto eventFilterDto = new EventFilterDto();
        long eventId = 1;
        eventController.getOwnedEvents(eventId, eventFilterDto);
        verify(eventService, times(1)).getOwnedEvents(eventId, eventFilterDto);
    }

    @Test
    void shouldGetParticipatedEventsController() {
        EventFilterDto eventFilterDto = new EventFilterDto();
        long eventId = 1;
        eventController.getParticipatedEvents(eventId, eventFilterDto);
        verify(eventService, times(1)).getParticipatedEvents(eventId, eventFilterDto);
    }
}