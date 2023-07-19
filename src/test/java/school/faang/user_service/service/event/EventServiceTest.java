package school.faang.user_service.service.event;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventEndDateFilter;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.filter.event.EventStartDateFilter;
import school.faang.user_service.filter.event.EventTitleFilter;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventValidator;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    EventDto eventDto2;
    EventDto eventDtoForUpdate;
    Event event;
    Event event1;
    EventDto eventDto;
    EventDto eventDto1;

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventValidator eventValidator;
    @Spy
    private EventMapperImpl eventMapper;
    @InjectMocks
    private EventService eventService;

    @BeforeEach
    public void init() {
        EventEndDateFilter eventEndDateFilter = new EventEndDateFilter();
        EventStartDateFilter eventStartDateFilter = new EventStartDateFilter();
        EventTitleFilter eventTitleFilter = new EventTitleFilter();
        List<EventFilter> filters = List.of(eventTitleFilter, eventStartDateFilter, eventEndDateFilter);
        eventService = new EventService(eventRepository, eventValidator, eventMapper, filters);

        eventDto2 = new EventDto(1L, "Cool new Event", LocalDateTime.now(), LocalDateTime.now(),
                0L, "hfgh", new ArrayList<>(), "location", 1);
        eventDtoForUpdate = new EventDto(2L, "Event 1", LocalDateTime.now(), LocalDateTime.now(),
                0L, "Description", new ArrayList<>(), "location", 1);
        event = Event.builder().id(1L).title("New Event").startDate(LocalDateTime.of(2022, Month.APRIL, 2, 0, 0))
                .endDate(LocalDateTime.of(2022, Month.APRIL, 3, 0, 0)).build();
        event1 = Event.builder().id(2L).title("Event 1").startDate(LocalDateTime.of(2022, Month.APRIL, 2, 0, 0))
                .endDate(LocalDateTime.of(2022, Month.APRIL, 3, 0, 0)).build();
        eventDto = new EventDto(1L, "New Event", LocalDateTime.now(), LocalDateTime.now(), 1L, "hfgh", new ArrayList<>(), "location", 1);
        eventDto1 = new EventDto(2L, "Event 1", LocalDateTime.now(), LocalDateTime.MAX, 1L, "hfdfgdgh", new ArrayList<>(), "location", 1);
    }

    @Test
    public void testCreateEvent() {
        eventService.createEvent(eventDto2);
        Mockito.verify(eventRepository, Mockito.times(1)).save(eventMapper.toEntity(eventDto2));
    }

    @Test
    public void testSkillsValidation() {
        eventValidator.checkIfUserHasSkillsRequired(eventDto2);
        Mockito.verify(eventValidator, Mockito.times(1)).checkIfUserHasSkillsRequired(eventDto2);
    }

    @Test
    public void testDeleteEvent() {
        eventService.deleteEvent(1L);
        Mockito.verify(eventRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void testGetParticipatedEvents() {
        Mockito.when(eventRepository.findParticipatedEventsByUserId(1L)).thenReturn(List.of(event, event1));
        Mockito.when(eventMapper.toDto(event)).thenReturn(eventDto);
        Mockito.when(eventMapper.toDto(event1)).thenReturn(eventDto1);
        List<EventDto> events = eventService.getParticipatedEvents(1L);
        assertEquals(2, events.size());
    }

    @Test
    public void testFilterEvent() {
        Mockito.when(eventRepository.findAll()).thenReturn(List.of(event, event1));

        EventFilterDto eventFilterDto = EventFilterDto.builder().titlePattern("New Event").build();
        List<EventDto> events = eventService.getEventsByFilter(eventFilterDto);
        assertEquals(1, events.size());
    }

    @Test
    public void testFilterStartDate() {
        Mockito.when(eventRepository.findAll()).thenReturn(List.of(event, event1));

        EventFilterDto eventFilterDto = EventFilterDto.builder().startDatePattern(LocalDateTime.of(2022, Month.APRIL, 1, 0, 0)).build();
        List<EventDto> events = eventService.getEventsByFilter(eventFilterDto);
        assertEquals(2, events.size());
    }

    @Test
    public void testFilterEndDate() {
        Mockito.when(eventRepository.findAll()).thenReturn(List.of(event, event1));

        EventFilterDto eventFilterDto = EventFilterDto.builder().endDatePattern(LocalDateTime.of(2022, Month.APRIL, 4, 0, 0)).build();
        List<EventDto> events = eventService.getEventsByFilter(eventFilterDto);
        assertEquals(2, events.size());
    }

    @Test
    public void testGetOwnedEvents() {
        Mockito.when(eventRepository.findAllByUserId(1L)).thenReturn(List.of(event, event1));
        Mockito.when(eventMapper.toDto(event)).thenReturn(eventDto);
        Mockito.when(eventMapper.toDto(event1)).thenReturn(eventDto1);
        List<EventDto> events = eventService.getOwnedEvents(1L);
        assertEquals(2, events.size());
    }

    @Test
    public void testUpdateEvent() {
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        eventService.updateEvent(1L, eventDtoForUpdate);
        Mockito.verify(eventRepository, Mockito.times(1)).save(eventMapper.toEntity(eventDtoForUpdate));
    }

    @Test
    public void testCreateEventWithMapper() {
        Event event = Event.builder().id(4L).maxAttendees(1).build();
        EventDto eventDto1 = new EventDto(4L, null, null, null, null, null, null, null, 1);
        Mockito.when(eventMapper.toEntity(eventDto1)).thenReturn(event);
        Mockito.when(eventRepository.save(event)).thenReturn(event);
        Mockito.when(eventMapper.toDto(event)).thenReturn(eventDto1);
        Assertions.assertEquals(eventDto1, eventService.createEvent(eventDto1));
    }

    @Test
    public void testGetEventThrowEntityNotFoundException() {
        Mockito.when(eventRepository.findById(1L)).thenThrow(new EntityNotFoundException("Event not found"));
        assertThrows(EntityNotFoundException.class, () -> eventService.getEvent(1L));
    }

    @Test
    public void testGetEvent() {
        EventDto eventDto = new EventDto(1L, null, null, null, null, null, null, null, 1);
        Event event = Event.builder().id(1L).maxAttendees(1).build();
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.ofNullable(event));
        Mockito.when(eventMapper.toDto(event)).thenReturn(eventDto);
        assertEquals(eventDto, eventService.getEvent(1L));
    }
}