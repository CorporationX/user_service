package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @InjectMocks
    private EventController eventController;

    @Mock
    private EventService eventService;

    @Mock
    private EventValidator eventValidator;

    private EventDto eventDto;
    private List<EventDto> eventDtoList;
    private EventFilterDto eventFilterDto;

    @BeforeEach
    void setUp() {
        eventDto = EventDto.builder().build();
    }

    @Test
    void create_shouldReturnEventDto() {
        // Arrange
        when(eventService.create(eventDto)).thenReturn(eventDto);

        // Act
        EventDto result = eventController.create(eventDto);

        // Assert
        verify(eventService, times(1)).create(eventDto);
        assertEquals(eventDto, result);
    }

    @Test
    void getEventsByFilter_shouldReturnFilteredEvents() {
        // Arrange
        when(eventService.getEventsByFilter(eq(eventFilterDto))).thenReturn(eventDtoList);

        // Act
        List<EventDto> result = eventController.getEventsByFilter(eventFilterDto);

        // Assert
        assertEquals(eventDtoList, result);
        verify(eventService).getEventsByFilter(eq(eventFilterDto));
    }

    @Test
    void deleteEvent_shouldCallServiceDeleteEvent() {
        // Arrange
        Long eventId = 1L;

        // Act
        eventController.deleteEvent(eventId);

        // Assert
        verify(eventService).deleteEvent(eventId);
    }

    @Test
    void updateEvent_shouldCallServiceUpdateEvent() {
        // Act
        eventService.updateEvent(eventDto);

        // Assert
        verify(eventService).updateEvent(eventDto);
    }

    @Test
    void getOwnedEvents_shouldCallServiceUpdateEvent() {
        // Act
        eventService.getOwnedEvents(1L);

        // Assert
        verify(eventService).getOwnedEvents(1L);
    }

    @Test
    void getParticipatedEvents_shouldCallServiceGetParticipatedEvents() {
        // Act
        eventService.getParticipatedEvents(1L);

        // Assert
        verify(eventService).getParticipatedEvents(1L);
    }
}
