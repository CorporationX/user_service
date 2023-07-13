package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    EventDto eventDto;
    EventDto eventDtoForUpdate;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventValidator eventValidator;
    @Mock
    private EventMapper eventMapper;
    @InjectMocks
    private EventService eventService;

    @BeforeEach
    public void init() {
        eventDto = new EventDto(1L, "Cool new Event", LocalDateTime.now(), LocalDateTime.now(),
                0L, "hfgh", new ArrayList<>(), "location", 1);
        eventDtoForUpdate = new EventDto(2L, "Event 1", LocalDateTime.now(), LocalDateTime.now(),
                0L, "Description", new ArrayList<>(), "location", 1);
    }

    @Test
    public void testCreateEvent() {
        eventService.createEvent(eventDto);
        Mockito.verify(eventRepository, Mockito.times(1)).save(eventMapper.toEntity(eventDto));
    }

    @Test
    public void testSkillsValidation() {
        eventValidator.checkIfUserHasSkillsRequired(eventDto);
        Mockito.verify(eventValidator, Mockito.times(1)).checkIfUserHasSkillsRequired(eventDto);
    }

    @Test
    public void testDeleteEvent() {
        long id = 3L;
        var event = Event.builder().id(id).build();
        Mockito.when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        eventService.deleteEvent(id);
        Mockito.verify(eventRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    public void testDeleteEventThrowsException() {
        long id = -1L;
        assertThrows(DataValidationException.class, () -> {
            eventService.deleteEvent(id);
        });
    }

    @Test
    public void testUpdateEvent() {
        var event = Event.builder().id(1L).title("New Event").build();
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        Mockito.when(eventMapper.toDto(event)).thenReturn(eventDtoForUpdate);
        EventDto updatedEvent = eventService.updateEvent(1L, eventDto);
        assertEquals("Cool new Event", updatedEvent.getTitle());
    }

    @Test
    public void testFilterEvent() {
        var event = Event.builder().id(1L).title("New Event").build();
        var event1 = Event.builder().id(2L).title("Event 1").build();
        var eventDto = new EventDto(1L, "New Event", LocalDateTime.now(), LocalDateTime.now(), 1L, "hfgh", new ArrayList<>(), "location", 1);
        var eventDto1 = new EventDto(2L, "Event 1", LocalDateTime.now(), LocalDateTime.MAX, 1L, "hfdfgdgh", new ArrayList<>(), "location", 1);

        Mockito.when(eventRepository.findAll()).thenReturn(List.of(event, event1));
        Mockito.when(eventMapper.toDto(event)).thenReturn(eventDto);
        Mockito.when(eventMapper.toDto(event1)).thenReturn(eventDto1);
        var filters = new EventFilterDto(null, null, null, null, null,
                null, null, "location", 1);

        List<EventDto> events = eventService.getEventsByFilter(filters);
        assertEquals(2, events.size());

    }

    @Test
    public void testGetOwnedEvents() {
        var event = Event.builder().id(1L).title("New Event").build();
        var event1 = Event.builder().id(2L).title("Event 1").build();
        var eventDto = new EventDto(1L, "New Event", LocalDateTime.now(), LocalDateTime.now(), 1L, "hfgh", new ArrayList<>(), "location", 1);
        var eventDto1 = new EventDto(2L, "Event 1", LocalDateTime.now(), LocalDateTime.MAX, 1L, "hfdfgdgh", new ArrayList<>(), "location", 1);
        Mockito.when(eventRepository.findAllByUserId(1L)).thenReturn(List.of(event, event1));
        Mockito.when(eventMapper.toDto(event)).thenReturn(eventDto);
        Mockito.when(eventMapper.toDto(event1)).thenReturn(eventDto1);
        List<EventDto> events = eventService.getOwnedEvents(1L);
        assertEquals(2, events.size());
    }
}