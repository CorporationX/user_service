package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.test_data.event.TestDataEvent;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class EventControllerTest {
    @Mock
    private EventService eventService;
    @InjectMocks
    private EventController eventController;

    private TestDataEvent testDataEvent;
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        testDataEvent = new TestDataEvent();

        eventDto = testDataEvent.getEventDto();
    }


    @Test
    void testCreateEvent_Success() {
        when(eventService.createEvent(eventDto)).thenReturn(eventDto);

        EventDto result = eventController.createEvent(eventDto);
        assertNotNull(result);
        assertEquals(eventDto, result);

        verify(eventService, atLeastOnce()).createEvent(eventDto);
    }

    @Test
    public void testGetEvent_Success() {
        when(eventService.getEvent(eventDto.getId())).thenReturn(eventDto);

        EventDto result = eventController.getEvent(eventDto.getId());
        assertNotNull(result);
        assertEquals(eventDto.getId(), result.getId());

        verify(eventService, atLeastOnce()).getEvent(eventDto.getId());
    }

    @Test
    void testGetEventsByFilter_Success() {
        EventFilterDto eventFilterDto = new EventFilterDto();
        List<EventDto> eventList = List.of(eventDto);

        when(eventService.getEventsByFilters(eventFilterDto)).thenReturn(eventList);

        List<EventDto> result = eventController.getEventsByFilter(eventFilterDto);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(eventDto.getId(), result.get(0).getId());

        verify(eventService, atLeastOnce()).getEventsByFilters(eventFilterDto);
    }

    @Test
    void testDeleteEvent_Success() {
        eventController.deleteEvent(eventDto.getId());

        verify(eventService, atLeastOnce()).deleteEvent(eventDto.getId());
    }

    @Test
    public void testUpdateEvent_Success() {
        when(eventService.updateEvent(eventDto)).thenReturn(eventDto);

        EventDto result = eventController.updateEvent(eventDto);
        assertNotNull(result);
        assertEquals(eventDto.getId(), result.getId());

        verify(eventService, atLeastOnce()).updateEvent(eventDto);
    }

    @Test
    public void testGetEventsOwner_Success() {
        List<EventDto> eventList = List.of(eventDto);

        when(eventService.getEventsOwner(eventDto.getOwnerId())).thenReturn(eventList);

        List<EventDto> result = eventController.getEventsOwner(eventDto.getOwnerId());
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(eventDto.getId(), result.get(0).getId());

        verify(eventService, atLeastOnce()).getEventsOwner(eventDto.getOwnerId());
    }

    @Test
    public void testGetEventParticipants_Success() {
        User user = testDataEvent.getUser();
        List<EventDto> eventList = List.of(eventDto);

        when(eventService.getEventParticipants(user.getId())).thenReturn(eventList);

        List<EventDto> result = eventController.getEventParticipants(user.getId());
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(eventDto.getId(), result.get(0).getId());

        verify(eventService, atLeastOnce()).getEventParticipants(user.getId());
    }
}
