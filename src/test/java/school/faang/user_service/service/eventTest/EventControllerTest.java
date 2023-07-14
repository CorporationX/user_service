package school.faang.user_service.service.eventTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.controller.event.EventController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class EventControllerTest {
    @Mock
    private EventService eventService;
    @InjectMocks
    private EventController eventController;
    EventDto eventDto;
    EventFilterDto filterDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        var now = LocalDateTime.now();
        eventDto = new EventDto(0L, "0", now, now.plusDays(3), 0L, "0", new ArrayList<>(), "location", -1);
        filterDto = new EventFilterDto("title", now, now.plusDays(10), 0L, List.of(), "location", 10);
    }

    @Test
    void testValidDto() {
        eventController.create(eventDto);
        verify(eventService, times(1)).create(eventDto);
    }

    @Test
    void testNullTitleIsInvalid() {
        eventDto.setTitle(null);
        var result = eventController.create(eventDto);
        Assertions.assertEquals(result, ResponseEntity.badRequest().body("Event title cannot be empty"));
    }

    @Test
    void testBlankTitleIsInvalid() {
        eventDto.setTitle("  ");
        var result = eventController.create(eventDto);
        Assertions.assertEquals(result, ResponseEntity.badRequest().body("Event title cannot be empty"));
    }

    @Test
    void testNullStartDateIsInvalid() {
        eventDto.setStartDate(null);
        var result = eventController.create(eventDto);
        Assertions.assertEquals(result, ResponseEntity.badRequest().body("Event start date cannot be null"));
    }

    @Test
    void testNullOwnedIdIsInvalid() {
        eventDto.setOwnerId(null);
        var result = eventController.create(eventDto);
        Assertions.assertEquals(result, ResponseEntity.badRequest().body("Event owner ID cannot be null or negative"));
    }

    @Test
    void testNegativeOwnedIdIsInvalid() {
        eventDto.setOwnerId(-1L);
        var result = eventController.create(eventDto);
        Assertions.assertEquals(result, ResponseEntity.badRequest().body("Event owner ID cannot be null or negative"));
    }

    @Test
    void testNegativeEventIdIsInvalid() {
        long eventId = -1;
        ResponseEntity<?> responseEntity = eventController.getEvent(eventId);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void testReceivingValidEvent() {
        int eventId = 10;
        eventController.getEvent(eventId);
        verify(eventService, times(1)).getEvent(eventId);
    }

    @Test
    void testCorrectReceivingFilteredEvent() {
        Mockito.when(eventService.getEventsByFilter(filterDto)).thenReturn(List.of(eventDto));
        Assertions.assertEquals(HttpStatus.OK, eventController.getEventsByFilter(filterDto).getStatusCode());
    }

    @Test
    void testReceivingFilteredEventWithException() {
        Mockito.when(eventService.getEventsByFilter(filterDto)).thenThrow(new NotFoundException("Not found"));
        var response = eventController.getEventsByFilter(filterDto);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(eventService, times(1)).getEventsByFilter(filterDto);
    }

    @Test
    void deletingEventNegativeIdExceptionTest() {
        var response = eventController.deleteEvent(-1);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void correctDeletingEventTest() {
        var response = eventController.deleteEvent(100);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
}
