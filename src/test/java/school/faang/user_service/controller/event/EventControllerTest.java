package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.event.EventDto;
import school.faang.user_service.model.event.EventFilterDto;
import school.faang.user_service.service.impl.event.EventServiceImpl;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;

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
    private EventServiceImpl eventServiceImpl;

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
        when(eventServiceImpl.create(eventDto)).thenReturn(eventDto);

        // Act
        EventDto result = eventController.create(eventDto);

        // Assert
        verify(eventServiceImpl, times(1)).create(eventDto);
        assertEquals(eventDto, result);
    }

    @Test
    void getEventsByFilter_shouldReturnFilteredEvents() {
        // Arrange
        when(eventServiceImpl.getEventsByFilter(eq(eventFilterDto))).thenReturn(eventDtoList);

        // Act
        List<EventDto> result = eventController.getEventsByFilter(eventFilterDto);

        // Assert
        assertEquals(eventDtoList, result);
        verify(eventServiceImpl).getEventsByFilter(eq(eventFilterDto));
    }

    @Test
    void deleteEvent_shouldCallServiceDeleteEvent() {
        // Arrange
        Long eventId = 1L;

        // Act
        eventController.deleteEvent(eventId);

        // Assert
        verify(eventServiceImpl).deleteEvent(eventId);
    }

    @Test
    void updateEvent_shouldCallServiceUpdateEvent() {
        // Act
        eventServiceImpl.updateEvent(eventDto);

        // Assert
        verify(eventServiceImpl).updateEvent(eventDto);
    }

    @Test
    void getOwnedEvents_shouldCallServiceUpdateEvent() {
        // Act
        eventServiceImpl.getOwnedEvents(1L);

        // Assert
        verify(eventServiceImpl).getOwnedEvents(1L);
    }

    @Test
    void getParticipatedEvents_shouldCallServiceGetParticipatedEvents() {
        // Act
        eventServiceImpl.getParticipatedEvents(1L);

        // Assert
        verify(eventServiceImpl).getParticipatedEvents(1L);
    }
}
