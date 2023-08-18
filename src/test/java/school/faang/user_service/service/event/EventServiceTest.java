package school.faang.user_service.service.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.commons.collections4.ListUtils;
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
import school.faang.user_service.dto.skill.UserSkillGuaranteeDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.mapper.skill.SkillMapperImpl;
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
    @Spy
    private SkillMapperImpl skillMapper;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventAsyncService eventAsyncService;

    private List<Event> events;
    EventDto eventDto;
    Event event;
    User user;
    Skill skill;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(200L);

        skill = new Skill();
        skill.setId(22L);
        skill.setTitle("Ability");
        skill.setGuarantees(List.of(
                UserSkillGuarantee.builder().id(31L).build()
        ));

        user.setSkills(List.of(skill));

        eventDto = EventDto.builder()
                .id(1L)
                .title("My Event")
                .startDate(LocalDateTime.of(2023, 7, 27, 10, 0))
                .endDate(LocalDateTime.of(2023, 7, 27, 15, 0))
                .ownerId(1L)
                .relatedSkills(List.of(
                        SkillDto.builder()
                                .id(22L)
                                .title("Ability")
                                .guarantees(List.of(
                                        UserSkillGuaranteeDto.builder()
                                                .id(31L)
                                                .build()
                                ))
                                .build()))
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
        List<Event> existingEvents = List.of(event);

        when(eventRepository.findAllByUserId(event.getOwner().getId())).thenReturn(existingEvents);

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreate_EventDoesNotExist_ReturnsDto() {
        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        when(eventMapper.toEvent(eventDto)).thenReturn(event);
        when(eventRepository.findAllByUserId(event.getOwner().getId())).thenReturn(new ArrayList<>());
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventDto result = eventService.create(eventDto);

        assertNotNull(result);
        assertEquals(eventDto.getId(), result.getId());
    }

    @Test
    void testCreate_UserHasNecessarySkills_ReturnsDto() {
        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        when(eventMapper.toEvent(eventDto)).thenReturn(event);
        when(eventRepository.findAllByUserId(event.getOwner().getId())).thenReturn(new ArrayList<>());
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

        Event result = eventService.getEvent(1L);

        assertNotNull(result);
        assertEquals(eventDto.getId(), result.getId());
    }

    @Test
    void testGetEvent_NonExistingEventId() {
        when(eventMapper.toDto(event)).thenReturn(eventDto);
        when(eventMapper.toDto(any(Event.class))).thenReturn(eventDto);
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.getEvent(1L));
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
        long userId = 1L;

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
        long nonExistingUserId = 100L;

        when(eventRepository.findAllByUserId(nonExistingUserId)).thenReturn(Collections.emptyList());

        List<EventDto> result = eventService.getOwnedEvents(nonExistingUserId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test getting participated events for existing user")
    void testGetParticipatedEventsForExistingUser() {
        long userId = 1L;

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
        long nonExistingUserId = 100L;

        when(eventRepository.findParticipatedEventsByUserId(nonExistingUserId)).thenReturn(Collections.emptyList());

        List<EventDto> result = eventService.getParticipatedEvents(nonExistingUserId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void clearEvents_shouldSplitEventListAndInvokeClearEventsPartition() {
        Event event = mock(Event.class);
        when(event.getEndDate()).thenReturn(LocalDateTime.now().minusDays(1));
        events = List.of(event, event, event);

        when(eventRepository.findAll()).thenReturn(events);

        eventService.clearEvents(1);

        List<List<Event>> partitions = ListUtils.partition(events, events.size());

        partitions.forEach(partition -> verify(eventAsyncService).clearEventsPartition(partition));
    }
}