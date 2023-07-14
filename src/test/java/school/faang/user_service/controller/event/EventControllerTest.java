package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void create_ValidEvent_ReturnsOkResponse() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("Event 1");
        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setOwnerId(1L);

        when(eventService.create(any(EventDto.class))).thenReturn(eventDto);

        ResponseEntity<EventDto> response = eventController.create(eventDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(eventDto, response.getBody());
        verify(eventService).create(eventDto);
    }

    @Test
    void create_InvalidEvent_ThrowsDataValidationException() {
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