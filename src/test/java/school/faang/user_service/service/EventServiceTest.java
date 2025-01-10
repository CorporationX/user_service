package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private UserService userService;

    private EventDto eventDto;
    private Event event;

    @BeforeEach
    void setUp() {
        eventDto = EventDto.builder()
                .id(1L)
                .title("Test Event")
                .build();

        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
    }

    @Test
    void shouldCreateEvent() {
        when(eventMapper.toEntity(eventDto)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        EventDto createdEvent = eventService.createEvent(eventDto);

        assertNotNull(createdEvent);
        assertEquals("Test Event", createdEvent.getTitle());
        verify(eventMapper).toEntity(eventDto);
        verify(eventRepository).save(event);
        verify(eventMapper).toDto(event);
    }

    @Test
    void shouldGetEventWhenExists() throws DataValidationException {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        EventDto fetchedEvent = eventService.getEvent(1L);

        assertNotNull(fetchedEvent);
        assertEquals(1L, fetchedEvent.getId());
        verify(eventRepository).findById(1L);
        verify(eventMapper).toDto(event);
    }

    @Test
    void shouldThrowExceptionWhenEventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> eventService.getEvent(1L)
        );

        assertEquals("Event not found with ID: 1", exception.getMessage());
        verify(eventRepository).findById(1L);
        verifyNoInteractions(eventMapper);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldGetEventsByFilters() {
        EventFilterDto filterDto = new EventFilterDto();
        List<Event> events = List.of(event);
        List<EventDto> expectedEventDtos = List.of(eventDto);
        when(eventRepository.findAll(any(Specification.class))).thenReturn(events);
        when(eventMapper.toDtoList(events)).thenReturn(expectedEventDtos);
        List<EventDto> eventDtos = eventService.getEventsByFilters(filterDto);
        assertNotNull(eventDtos);
        assertEquals(1, eventDtos.size());
        assertEquals("Test Event", eventDtos.get(0).getTitle());
        verify(eventRepository).findAll(any(Specification.class));
        verify(eventMapper).toDtoList(events);
    }

    @Test
    void shouldDeleteEventWhenExists() throws DataValidationException {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        eventService.deleteEvent(1L);

        verify(eventRepository).findById(1L);
        verify(eventRepository).delete(event);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> eventService.deleteEvent(1L)
        );

        assertEquals("Event not found with ID: 1", exception.getMessage());
        verify(eventRepository).findById(1L);
        verify(eventRepository, never()).delete(any(Event.class));
    }
}