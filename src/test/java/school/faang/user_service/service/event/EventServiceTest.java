package school.faang.user_service.service.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.event.DataValidationException;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventServiceTest {
    @InjectMocks
    private EventService eventService;
    @Spy
    private EventMapperImpl eventMapper;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    EventDto eventDto;
    Event event;
    User user;
    Skill skill;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Ability");

        user.setSkills(List.of(skill));

        eventDto = EventDto.builder()
                .id(1L)
                .title("My Event")
                .startDate(LocalDateTime.of(2023, 7, 27, 10, 0))
                .endDate(LocalDateTime.of(2023, 7, 27, 15, 0))
                .ownerId(1L)
                .relatedSkills(List.of(SkillDto.builder().id(1L).title("Ability").build()))
                .location("Conference Hall")
                .maxAttendees(100)
                .build();

        event = Event.builder()
                .id(1L)
                .title("My Event")
                .startDate(LocalDateTime.of(2023, 7, 27, 10, 0))
                .endDate(LocalDateTime.of(2023, 7, 27, 15, 0))
                .owner(user)
                .relatedSkills(List.of(skill))
                .location("Conference Hall")
                .maxAttendees(100)
                .build();
    }

    @Test
    void testCreate_EventExists() {
        List<Event> existingEvents = new ArrayList<>();
        existingEvents.add(event);

        when(eventRepository.findByEventId(event.getId())).thenReturn(existingEvents);

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreate_EventDoesNotExist_ReturnsDto() {
        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        when(eventMapper.toEvent(eventDto)).thenReturn(event);
        when(eventRepository.findByEventId(event.getId())).thenReturn(new ArrayList<>());
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventDto result = eventService.create(eventDto);

        assertNotNull(result);
        assertEquals(eventDto.getId(), result.getId());
    }

    @Test
    void testCreate_ValidOwnerId() {
        eventDto.setOwnerId(1L);

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreate_IfOwnerIdNull() {
        eventDto.setOwnerId(null);

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreate_IfOwnerIdNegative() {
        eventDto.setOwnerId(-1L);

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreate_ValidEventTitle() {
        eventDto.setTitle("My Title");

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreate_NullEventTitle() {
        eventDto.setTitle(null);

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreate_BlankEventTitle() {
        eventDto.setTitle("     ");

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreate_UserHasNecessarySkills_ReturnsDto() {
        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        when(eventMapper.toEvent(eventDto)).thenReturn(event);
        when(eventRepository.findByEventId(event.getId())).thenReturn(new ArrayList<>());
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventDto result = eventService.create(eventDto);

        assertNotNull(result);
        assertEquals(eventDto.getId(), result.getId());
    }

    @Test
    void testCreate_UserDoesNotHaveNecessarySkills() {
        eventDto.setRelatedSkills(List.of(SkillDto.builder().id(2L).title("Expertise").build()));

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testGetEvent_ExistingEventId_ReturnEventDto() {
        when(eventMapper.toDto(event)).thenReturn(eventDto);
        when(eventMapper.toDto(any(Event.class))).thenReturn(eventDto);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        EventDto result = eventService.getEvent(1L);

        assertNotNull(result);
        assertEquals(eventDto.getId(), result.getId());
    }

    @Test
    void testGetEvent_NonExistingEventId() {
        when(eventMapper.toDto(event)).thenReturn(eventDto);
        when(eventMapper.toDto(any(Event.class))).thenReturn(eventDto);
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> eventService.getEvent(1L));
    }

    @Test
    @DisplayName("Test getting events by filters")
    void testGetEventsByFilter() {
        List<Event> events = new ArrayList<>();
        events.add(event);

        when(eventRepository.findAll()).thenReturn(events);

        EventFilterDto filters = EventFilterDto.builder()
                .titlePattern("My Event")
                .build();

        List<EventDto> filteredEvents = eventService.getEventsByFilter(null, filters);

        assertNotNull(filteredEvents);
        assertFalse(filteredEvents.isEmpty());
        assertTrue(filteredEvents.stream().allMatch(e -> e.getTitle().equals("My Event")));
    }

    @Test
    @DisplayName("Test getting events by non-existing filter")
    void testGetEventsByNonExistingFilter() {
        List<Event> events = new ArrayList<>();
        events.add(event);

        when(eventRepository.findAll()).thenReturn(events);

        EventFilterDto filters = EventFilterDto.builder()
                .titlePattern("Non-Existing Event")
                .build();

        List<EventDto> filteredEvents = eventService.getEventsByFilter(null, filters);

        assertTrue(filteredEvents.isEmpty());
    }

    @Test
    @DisplayName("Test applying filters to events")
    void testApplyFilters() {
        List<Event> events = new ArrayList<>();
        events.add(event);

        EventFilterDto filters = EventFilterDto.builder()
                .titlePattern("My Event")
                .locationPattern("Conference")
                .build();

        Stream<Event> filteredEvents = eventService.applyFilters(events.stream(), filters);

        List<Event> filteredEventsList = filteredEvents.toList();

        assertNotNull(filteredEventsList);
        assertFalse(filteredEventsList.isEmpty());
        assertTrue(filteredEventsList.stream().allMatch(e -> e.getTitle().equals("My Event") && e.getLocation().contains("Conference")));
    }

    @Test
    @DisplayName("Test applying empty filters")
    void testApplyEmptyFilters() {
        List<Event> events = new ArrayList<>();
        events.add(event);

        EventFilterDto filters = EventFilterDto.builder().build();

        Stream<Event> filteredEvents = eventService.applyFilters(events.stream(), filters);

        List<Event> filteredEventsList = filteredEvents.toList();

        assertNotNull(filteredEventsList);
        assertEquals(events.size(), filteredEventsList.size());
    }

    @Test
    @DisplayName("Test deleting an existing event by ID")
    void testDeleteExistingEvent() {
        Long eventIdToDelete = 1L;

        when(eventRepository.findById(eventIdToDelete)).thenReturn(Optional.of(event));

        eventService.deleteEvent(eventIdToDelete);

        verify(eventRepository, times(1)).delete(event);
    }

    @Test
    @DisplayName("Test deleting a non-existing event by ID")
    void testDeleteNonExistingEvent() {
        Long nonExistingEventId = 100L;

        when(eventRepository.findById(nonExistingEventId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> eventService.deleteEvent(nonExistingEventId));

        verify(eventRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Test getting owned events for existing user")
    void testGetOwnedEventsForExistingUser() {
        Long userId = 1L;

        List<Event> ownedEvents = List.of(event);

        when(eventRepository.findAllByUserId(userId)).thenReturn(ownedEvents);

        List<EventDto> result = eventService.getOwnedEvents(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(event.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("Test getting owned events for non-existing user")
    void testGetOwnedEventsForNonExistingUser() {
        Long nonExistingUserId = 100L;

        when(eventRepository.findAllByUserId(nonExistingUserId)).thenReturn(Collections.emptyList());

        List<EventDto> result = eventService.getOwnedEvents(nonExistingUserId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test getting participated events for existing user")
    void testGetParticipatedEventsForExistingUser() {
        Long userId = 1L;

        List<Event> participatedEvents = List.of(event);

        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(participatedEvents);

        List<EventDto> result = eventService.getParticipatedEvents(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(event.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("Test getting participated events for non-existing user")
    void testGetParticipatedEventsForNonExistingUser() {
        Long nonExistingUserId = 100L;

        when(eventRepository.findParticipatedEventsByUserId(nonExistingUserId)).thenReturn(Collections.emptyList());

        List<EventDto> result = eventService.getParticipatedEvents(nonExistingUserId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //ТЕСТЫ К МЕТОДУ updateEvent() тут не написаны, т.к. они уже есть в соответствующих тестах к Мапперам..
}