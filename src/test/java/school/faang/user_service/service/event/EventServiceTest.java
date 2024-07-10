package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    private EventService eventService;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private EventMapper eventMapper;
    @Spy
    private EventDescriptionFilter eventDescriptionFilter = new EventDescriptionFilter();
    @Spy
    private EventOwnerFilter eventOwnerFilter = new EventOwnerFilter();
    private List<EventFilter> eventFilters;
    private Long eventId = 1L;
    private Long ownerId = 2L;
    private Skill skill = new Skill();
    private User owner = User.builder()
            .id(ownerId)
            .build();
    private Event event = Event.builder()
            .id(eventId)
            .relatedSkills(List.of(skill))
            .owner(owner)
            .build();
    private EventDto eventDto = new EventDto();

    {
        eventDto.setId(eventId);
        eventDto.setOwnerId(ownerId);
    }

    private User user = new User();

    @BeforeEach
    void setUp() {
        eventRepository = Mockito.mock(EventRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        eventMapper = Mockito.mock(EventMapper.class);
        eventFilters = List.of(eventDescriptionFilter, eventOwnerFilter);
        eventService = new EventService(eventRepository, userRepository, eventMapper, eventFilters);
    }

    @Test
    public void testCreateWithoutUserSkills() {
        user.setSkills(Collections.emptyList());

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        when(eventMapper.toEntity(eventDto, userRepository)).thenReturn(event);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
        assertEquals("User hasn't required skills", exception.getMessage());
        verify(eventRepository, times(0)).save(event);
        verify(eventMapper, times(0)).toDto(eventRepository.save(event));
    }

    @Test
    public void testCreateWithRelatedSkills() {
        user.setSkills(List.of(skill));

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        when(eventMapper.toEntity(eventDto, userRepository)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);
        when(eventRepository.save(event)).thenReturn(event);

        eventService.create(eventDto);

        verify(eventRepository, times(1)).save(event);
    }

    @Test
    public void testGetEventNotExisting() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> eventService.getEvent(eventId));
        assertEquals("Event not found for ID: " + eventId, exception.getMessage());
    }

    @Test
    public void testGetEventExisting() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(any(Event.class))).thenReturn(eventDto);

        EventDto result = eventService.getEvent(eventId);

        assertNotNull(result);
        assertEquals(eventId, result.getId());
        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    public void testGetEventsByFilter() {
        Event event1 = Event.builder()
                .description("Event for IT-founders")
                .owner(owner)
                .build();
        Event event2 = Event.builder()
                .description("Event for IT-founders")
                .build();
        Event event3 = Event.builder()
                .owner(owner)
                .build();
        List<Event> events = List.of(event1, event2, event3);

        when(eventRepository.findAll()).thenReturn(events);
        EventFilterDto filters = new EventFilterDto();
        filters.setOwnerId(ownerId);
        filters.setDescriptionPattern("IT");

        when(eventMapper.toDto(event1)).thenReturn(eventDto);

        List<EventDto> result = eventService.getEventsByFilter(filters);
        List<EventDto> expected = List.of(eventDto);

        verify(eventDescriptionFilter, times(1)).isApplicable(filters);
        verify(eventOwnerFilter, times(1)).isApplicable(filters);
        verify(eventDescriptionFilter, times(1)).apply(any(), eq(filters));
        verify(eventOwnerFilter, times(1)).apply(any(), eq(filters));
        assertEquals(expected, result);
    }

    @Test
    public void testDeleteEvent() {
        eventService.deleteEvent(eventId);

        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    public void testUpdateWithoutUserSkills() {
        user.setSkills(Collections.emptyList());

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        when(eventMapper.toEntity(eventDto, userRepository)).thenReturn(event);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
        assertEquals("User hasn't required skills", exception.getMessage());
        verify(eventRepository, times(0)).save(event);
        verify(eventMapper, times(0)).toDto(eventRepository.save(event));
    }

    @Test
    public void testUpdateWithRelatedSkills() {
        user.setSkills(List.of(skill));

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        when(eventMapper.toEntity(eventDto, userRepository)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);
        when(eventRepository.save(event)).thenReturn(event);

        eventService.updateEvent(eventDto);

        verify(eventRepository, times(1)).save(event);
    }

    @Test
    public void testGetOwnedEvents() {
        List<Event> events = List.of(event);

        when(eventRepository.findAllByUserId(ownerId)).thenReturn(events);
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        List<EventDto> result = eventService.getOwnedEvents(ownerId);
        List<EventDto> expected = List.of(eventDto);

        verify(eventRepository, times(1)).findAllByUserId(ownerId);
        assertEquals(expected, result);
    }

    @Test
    public void testGetParticipatedEvents() {
        List<Event> events = List.of(event);

        when(eventRepository.findParticipatedEventsByUserId(ownerId)).thenReturn(events);
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        List<EventDto> result = eventService.getParticipatedEvents(ownerId);
        List<EventDto> expected = List.of(eventDto);

        verify(eventRepository, times(1)).findParticipatedEventsByUserId(ownerId);
        assertEquals(expected, result);
    }
}
