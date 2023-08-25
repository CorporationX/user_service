package school.faang.user_service.service.eventTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import school.faang.user_service.controller.event.EventController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.invalidFieldException.DataValidationException;
import school.faang.user_service.exception.notFoundExceptions.event.EventNotFoundException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
public class EventControllerTest {
    @Mock
    private EventService eventService;
    @InjectMocks
    private EventController eventController;
    EventDto eventDto;
    EventFilterDto filterDto;

    @BeforeEach
    void setUp() {
        var now = LocalDateTime.now();
        eventDto = new EventDto(0L, "0", now, now.plusDays(3), 0L, "0", new ArrayList<>(),new ArrayList<>(), "location", EventType.WEBINAR, EventStatus.PLANNED, -1);
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
        Assertions.assertThrows(
                DataValidationException.class,
                () -> eventController.create(eventDto)
        );
    }

    @Test
    void testBlankTitleIsInvalid() {
        eventDto.setTitle("  ");
        Assertions.assertThrows(
                DataValidationException.class,
                () -> eventController.create(eventDto)
        );
    }

    @Test
    void testNullStartDateIsInvalid() {
        eventDto.setStartDate(null);
        Assertions.assertThrows(
                DataValidationException.class,
                () -> eventController.create(eventDto)
        );
    }

    @Test
    void testNullOwnedIdIsInvalid() {
        eventDto.setOwnerId(null);
        Assertions.assertThrows(
                DataValidationException.class,
                () -> eventController.create(eventDto)
        );
    }

    @Test
    void testNegativeEventIdIsInvalid() {
        long eventId = -1;
        Mockito.when(eventService.getEvent(eventId))
                        .thenThrow(new EventNotFoundException("Event with id: " + eventId + " was not found"));
        Assertions.assertThrows(
                EventNotFoundException.class,
                () -> eventController.getEvent(eventId)
        );
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
        Mockito.when(eventService.getEventsByFilter(filterDto)).thenThrow(new EventNotFoundException("Not found"));
        Assertions.assertThrows(
                EventNotFoundException.class,
                () -> eventController.getEventsByFilter(filterDto)
        );
        verify(eventService, times(1)).getEventsByFilter(filterDto);
    }

    @Test
    void correctDeletingEventTest() {
        var response = eventController.deleteEvent(100);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testUpdatingWithNullTitleIsInvalid() {
        eventDto.setTitle(null);
        Assertions.assertThrows(
                DataValidationException.class,
                () -> eventController.updateEvent(eventDto)
        );
    }

    @Test
    void testUpdatingWithNullStartDateIsInvalid() {
        eventDto.setStartDate(null);
        Assertions.assertThrows(
                DataValidationException.class,
                () -> eventController.updateEvent(eventDto)
        );
    }

    @Test
    void testUpdatingValidDto() {
        var result = eventController.updateEvent(eventDto);
        Assertions.assertEquals(result.getStatusCode(), HttpStatus.OK);
        verify(eventService, times(1)).updateEvent(eventDto);
    }

    @Test
    void testCorrectReceivingOwnedEvent() {
        long eventId = 31;

        var result = eventController.getOwnedEvents(eventId);

        Assertions.assertEquals(result.getStatusCode(), HttpStatus.OK);
        verify(eventService, times(1)).getOwnedEvents(eventId);
    }

    @Test
    void testReceivingOwnedEventWithInvalidId() {
        long eventId = Integer.MIN_VALUE;
        Mockito.when(eventService.getOwnedEvents(eventId))
                .thenThrow(new EventNotFoundException("Event with id: " + eventId + " was not found"));
        Assertions.assertThrows(
                EventNotFoundException.class,
                () -> eventController.getOwnedEvents(eventId)
        );
    }

    @Test
    void testGetParticipatedEventsWithNegativeId() {
        long eventId = Integer.MIN_VALUE;
        Mockito.when(eventService.getParticipatedEvents(eventId))
                        .thenThrow(new EventNotFoundException("Event with id: " + eventId + " was not found"));
        Assertions.assertThrows(
                EventNotFoundException.class,
                () -> eventController.getParticipatedEvents(eventId)
        );
    }

    @Test
    void testGetParticipatedEvents() {
        long eventId = 12;

        var result = eventController.getParticipatedEvents(eventId);

        Assertions.assertEquals(result.getStatusCode(), HttpStatus.OK);
    }
}
