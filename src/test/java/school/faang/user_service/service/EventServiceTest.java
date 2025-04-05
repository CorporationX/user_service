package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventValidation;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventValidation validation;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private List<EventFilter> eventFilters;

    public static final long EVENT_ID = 1L;
    public static final long USER_ID = 1L;

    EventDto eventDto = new EventDto();
    Event event = new Event();

    @Test
    void testCreateWithValidEvent() {
        when(eventMapper.toEntity(eventDto)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        EventDto createdEvent = eventService.create(eventDto);

        assertEquals(createdEvent, eventDto);
        verify(eventMapper).toEntity(eventDto);
        verify(eventRepository, times(1)).save(event);
        verify(eventMapper).toDto(event);
    }

    @Test
    void testGetEventWithValidId() {
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        EventDto result = eventService.getEvent(EVENT_ID);

        assertEquals(eventDto, result);
        verify(eventRepository, times(1)).findById(EVENT_ID);
    }

    @Test
    void testGetEventsByFilter() {
        EventFilterDto filters = EventFilterDto.builder().build();
        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        List<EventDto> result = eventService.getEventsByFilter(filters);

        assertEquals(1, result.size());
        assertEquals(eventDto, result.get(0));
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void testDeleteEvent() {
        doNothing().when(eventRepository).deleteById(EVENT_ID);

        eventService.deleteEvent(EVENT_ID);

        verify(eventRepository, times(1)).deleteById(EVENT_ID);
    }

    @Test
    void testUpdateEvent() {
        when(eventMapper.toEntity(eventDto)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        EventDto result = eventService.updateEvent(eventDto);

        assertEquals(eventDto, result);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void testGetOwnedEvents() {
        when(eventRepository.findAllByUserId(USER_ID)).thenReturn(List.of(event));
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        List<EventDto> result = eventService.getOwnedEvents(USER_ID);

        assertEquals(1, result.size());
        assertEquals(eventDto, result.get(0));
    }

    @Test
    void testGetParticipatedEvents() {
        when(eventRepository.findParticipatedEventsByUserId(USER_ID)).thenReturn(List.of(event));
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        List<EventDto> result = eventService.getParticipatedEvents(USER_ID);

        assertEquals(1, result.size());
        assertEquals(eventDto, result.get(0));
    }
}