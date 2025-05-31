package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import school.faang.user_service.controller.utils.EventControllerUtils;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    private static final Long TEST_ID = 1L;
    private static final int ONE = 1;

    @Mock
    private EventService eventService;

    @Mock
    private EventControllerUtils eventControllerUtils;

    @InjectMocks
    private EventController eventController;

    @Test
    public void testCreateEvent() {
        EventDto eventDto = new EventDto();
        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setTitle("Test Event");
        eventDto.setOwnerId(1L);
        EventDto saved = new EventDto();
        saved.setId(TEST_ID);
        when(eventService.create(eventDto)).thenReturn(saved);
        doNothing().when(eventControllerUtils).isValidDateRange(eventDto);

        EventDto result = eventController.create(eventDto);

        assertEquals(saved, result);
        verify(eventService).create(eventDto);
    }

    @Test
    public void testCreateEventWithUnValidDateRange() {
        EventDto eventDto = new EventDto();
        doThrow(new DataValidationException("End date should be after Start date"))
                .when(eventControllerUtils).isValidDateRange(eventDto);

        assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
    }

    @Test
    public void testGetEvent() {
        Long id = TEST_ID;
        EventDto expected = new EventDto();
        expected.setId(id);
        when(eventService.getEvent(id)).thenReturn(expected);

        EventDto result = eventController.getEvent(id);

        assertEquals(expected, result);
        verify(eventService).getEvent(id);
    }

    @Test
    public void testGetEventsByFilter() {
        EventFilterDto filter = new EventFilterDto();
        EventDto expected = new EventDto();
        expected.setId(1L);
        when(eventService.getEventsByFilter(filter, PageRequest.of(0, 10), 1L)).thenReturn(List.of(expected));

        List<EventDto> result = eventController.getEventsByFilter(filter, 0, 10, 1L);

        assertEquals(ONE, result.size());
        assertEquals(TEST_ID, result.get(0).getId());
        verify(eventService).getEventsByFilter(filter, PageRequest.of(0, 10), 1L);
    }

    @Test
    public void testGetEventsByFilterWithValidDateFilter() {
        EventFilterDto filter = new EventFilterDto();
        doNothing().when(eventControllerUtils).isValidDateRange(filter);
        filter.setEventType("EVENT");
        filter.setStartDate(LocalDateTime.now());
        filter.setEndDate(LocalDateTime.now().plusDays(ONE));
        EventDto expected = new EventDto();
        expected.setId(1L);
        when(eventService.getEventsByFilter(filter, PageRequest.of(0, 10), 1L)).thenReturn(List.of(expected));

        assertEquals(List.of(expected), eventController.getEventsByFilter(filter, 0, 10, 1L));
    }

    @Test
    public void testDeleteEvent() {
        Long id = TEST_ID;

        eventController.deleteEvent(id);

        verify(eventService).deleteEvent(id);
    }

    @Test
    public void testUpdateEvent() {
        EventDto input = new EventDto();
        input.setStartDate(LocalDateTime.now());
        input.setEndDate(LocalDateTime.now().plusDays(ONE));
        EventDto updated = new EventDto();
        updated.setId(TEST_ID);
        when(eventService.updateEvent(input)).thenReturn(updated);

        EventDto result = eventController.updateEvent(input);

        assertEquals(updated, result);
        verify(eventService).updateEvent(input);
    }

    @Test
    public void testGetOwnedEvents() {
        Long userId = TEST_ID;
        EventDto expected = new EventDto();
        expected.setId(TEST_ID);
        when(eventService.getOwnedEvents(userId)).thenReturn(List.of(expected));

        List<EventDto> result = eventController.getOwnedEvents(userId);

        assertEquals(ONE, result.size());
        assertEquals(TEST_ID, result.get(0).getId());
    }

    @Test
    public void testGetParticipatedEvents() {
        Long userId = TEST_ID;
        EventDto expected = new EventDto();
        expected.setId(TEST_ID);
        when(eventService.getParticipatedEvents(userId)).thenReturn(List.of(expected));

        List<EventDto> result = eventController.getParticipatedEvents(userId);

        assertEquals(ONE, result.size());
        assertEquals(TEST_ID, result.get(0).getId());
    }
}