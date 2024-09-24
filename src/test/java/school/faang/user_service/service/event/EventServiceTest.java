package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    private EventDto eventDto;
    private User user;
    private Skill skill1;
    private Skill skill2;
    private List<Skill> skills;
    private Event event;

    @InjectMocks
    private EventService eventService;

    @Mock
    EventRepository eventRepository;

    @Mock
    EventMapper eventMapper;

    @Mock
    SkillRepository skillRepository;

    @Mock
    EventValidator eventValidator;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        eventDto = EventDto.builder()
                .id(1L)
                .title("Новое событие")
                .startDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                .endDate(LocalDateTime.of(2024, 10, 1, 12, 0))
                .ownerId(1L)
                .description("description")
                .relatedSkills(List.of(1L, 2L))
                .location("location")
                .maxAttendees(5)
                .build();

        user = User.builder()
                .id(1L)
                .build();

        skill1 = Skill.builder()
                .id(1L)
                .build();

        skill2 = Skill.builder()
                .id(2L)
                .build();

        skills = List.of(skill1, skill2);

        event = Event.builder()
                .id(1L)
                .title("Новое событие")
                .description("какое-то описание")
                .startDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                .endDate(LocalDateTime.of(2024, 10, 1, 12, 0))
                .location("location")
                .maxAttendees(5)
                .owner(user)
                .relatedSkills(skills)
                .type(EventType.GIVEAWAY)
                .status(EventStatus.IN_PROGRESS)
                .build();
    }

    @Test
    @DisplayName("successful event creation")
    void whenCreateThenSaveEvent() {
        {
            Event savedEvent = new Event();
            EventDto resultDto = new EventDto();

            doNothing().when(eventValidator).validateEventDto(eventDto);
            doNothing().when(eventValidator).validateOwnerSkills(eventDto);
            when(eventMapper.toEvent(eventDto)).thenReturn(event);
            when(skillRepository.findAllByUserId(eventDto.getOwnerId())).thenReturn(skills);
            when(userRepository.getById(eventDto.getOwnerId())).thenReturn(user);
            when(eventRepository.save(event)).thenReturn(event);
            when(eventMapper.toDto(event)).thenReturn(eventDto);

            EventDto returnedDto = eventService.create(eventDto);

            assertNotNull(event.getRelatedSkills());
            assertNotNull(event.getOwner());

            verify(eventValidator).validateEventDto(eventDto);
            verify(eventValidator).validateOwnerSkills(eventDto);
            verify(eventMapper).toEvent(eventDto);
            verify(skillRepository).findAllByUserId(eventDto.getOwnerId());
            verify(userRepository).getById(eventDto.getOwnerId());
            verify(eventRepository).save(event);
            verify(eventMapper).toDto(event);

            assertEquals(eventDto, returnedDto);
        }
    }

    @Test
    void testGetEventWhenEventExists() {
        long eventId = 1L;
        Event event = mock(Event.class);
        EventDto expectedEventDto = mock(EventDto.class);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(expectedEventDto);

        EventDto result = eventService.getEvent(eventId);

        verify(eventRepository).findById(eventId);
        verify(eventMapper).toDto(event);
        assertEquals(expectedEventDto, result);
    }

    @Test
    void testGetEventWhenEventDoesNotExist() {
        long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> eventService.getEvent(eventId));
        assertEquals("Event not found with id: " + eventId, exception.getMessage());
        verify(eventRepository).findById(eventId);
    }

    @Test
    void testDeleteEvent() {
        doNothing().when(eventRepository).deleteById(event.getId());

        eventService.deleteEvent(event.getId());

        verify(eventRepository).deleteById(event.getId());
    }

    @Test
    void testUpdateEventSuccess() {
        when(eventRepository.findById(eventDto.getId())).thenReturn(Optional.of(event));

        doNothing().when(eventValidator).validateEventDto(eventDto);
        doNothing().when(eventValidator).validateOwnerSkills(eventDto);

        when(eventMapper.toEvent(eventDto)).thenReturn(event);
        when(skillRepository.findAllByUserId(eventDto.getOwnerId())).thenReturn(skills);
        when(userRepository.getById(eventDto.getOwnerId())).thenReturn(user);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        EventDto result = eventService.updateEvent(eventDto);

        assertNotNull(event.getRelatedSkills());
        assertNotNull(event.getOwner());

        verify(eventRepository).findById(eventDto.getId());
        verify(eventValidator).validateEventDto(eventDto);
        verify(eventValidator).validateOwnerSkills(eventDto);
        verify(eventMapper).toEvent(eventDto);
        verify(eventRepository).save(event);
        verify(eventMapper).toDto(event);
        assertEquals(eventDto, result);
    }

    @Test
    void testUpdateEventWhenUserIsNotOwner() {
        when(eventRepository.findById(eventDto.getId())).thenReturn(Optional.of(event));
        user.setId(2L);
        event.setOwner(user);
        assertThrows(DataValidationException.class,
                () -> eventService.updateEvent(eventDto));
        verify(eventRepository).findById(eventDto.getId());
    }

    @Test
    void testGetOwnedEvents() {
        long userId = 100L;
        List<Event> events = Arrays.asList(mock(Event.class), mock(Event.class));
        EventDto eventDto1 = mock(EventDto.class);
        EventDto eventDto2 = mock(EventDto.class);

        when(eventRepository.findAllByUserId(userId)).thenReturn(events);
        when(eventMapper.toDto(events.get(0))).thenReturn(eventDto1);
        when(eventMapper.toDto(events.get(1))).thenReturn(eventDto2);

        List<EventDto> result = eventService.getOwnedEvents(userId);

        verify(eventRepository).findAllByUserId(userId);
        verify(eventMapper).toDto(events.get(0));
        verify(eventMapper).toDto(events.get(1));
        assertEquals(Arrays.asList(eventDto1, eventDto2), result);
    }

    @Test
    void testGetParticipatedEvents() {
        long userId = 100L;
        List<Event> events = Arrays.asList(mock(Event.class), mock(Event.class));
        EventDto eventDto1 = mock(EventDto.class);
        EventDto eventDto2 = mock(EventDto.class);

        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(events);
        when(eventMapper.toDto(events.get(0))).thenReturn(eventDto1);
        when(eventMapper.toDto(events.get(1))).thenReturn(eventDto2);

        List<EventDto> result = eventService.getParticipatedEvents(userId);

        verify(eventRepository).findParticipatedEventsByUserId(userId);
        verify(eventMapper).toDto(events.get(0));
        verify(eventMapper).toDto(events.get(1));
        assertEquals(Arrays.asList(eventDto1, eventDto2), result);
    }

    @Test
    void testGetEventsByFilterWhenFiltersAreNull() {
        EventFilterDto filters = null;

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventService.getEventsByFilter(filters));
        assertEquals("filters is null", exception.getMessage());
    }

    @Test
    void testGetEventsByFilter() {
        EventFilterDto filters = mock(EventFilterDto.class);
        List<Event> allEvents = Arrays.asList(mock(Event.class), mock(Event.class));
        EventDto eventDto1 = mock(EventDto.class);
        EventDto eventDto2 = mock(EventDto.class);

        when(eventRepository.findAll()).thenReturn(allEvents);
        when(eventMapper.toDto(allEvents.get(0))).thenReturn(eventDto1);
        when(eventMapper.toDto(allEvents.get(1))).thenReturn(eventDto2);


        EventFilter filter = mock(EventFilter.class);
        when(filter.isApplicable(filters)).thenReturn(true);
        when(filter.apply(any(), eq(filters))).thenReturn(allEvents.stream());

        eventService = new EventService(eventMapper, eventRepository, Arrays.asList(filter),
                eventValidator, skillRepository, userRepository);

        List<EventDto> result = eventService.getEventsByFilter(filters);

        verify(eventRepository).findAll();
        verify(filter).isApplicable(filters);
        verify(filter).apply(any(), eq(filters));
        verify(eventMapper).toDto(allEvents.get(0));
        verify(eventMapper).toDto(allEvents.get(1));
        assertEquals(Arrays.asList(eventDto1, eventDto2), result);
    }
}