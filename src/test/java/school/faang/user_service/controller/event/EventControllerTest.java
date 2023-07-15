package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EventControllerTest {
    @Mock
    private EventService eventService;

    private EventController eventController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        eventController = new EventController(eventService);
    }

    @Test
    public void testCreateEvent_Success() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("My Event");
        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setOwnerId(1L);

        EventService eventService = mock(EventService.class);
        when(eventService.create(eventDto)).thenReturn(eventDto);

        EventController eventController = new EventController(eventService);

        ResponseEntity<EventDto> response = eventController.create(eventDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(eventDto, response.getBody());
        verify(eventService, times(1)).create(eventDto);
    }

    @Test
    void testCreateInvalidEvent_ThrowsDataValidationException() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle(null);
        eventDto.setStartDate(null);
        eventDto.setOwnerId(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            eventController.create(eventDto);
        });

        assertEquals("Event title must not be empty", exception.getMessage());
    }

}