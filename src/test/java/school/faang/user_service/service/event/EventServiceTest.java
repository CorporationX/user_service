package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.redis.EventStartDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.filter.event.EventDateFilter;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.filter.event.EventIdFilter;
import school.faang.user_service.filter.event.EventMaxAttendeesFilter;
import school.faang.user_service.filter.event.EventTitleFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.publisher.EventStartPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    private final EventMapper eventMapper = EventMapper.INSTANCE;
    @Mock
    private EventStartPublisher eventStartPublisher;

    @Mock
    private EventAsyncService eventAsyncService;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        List<EventFilter> eventFilters = List.of(
                new EventIdFilter(),
                new EventTitleFilter(),
                new EventDateFilter(),
                new EventMaxAttendeesFilter()
        );
        eventService = new EventService(eventRepository, userRepository, eventFilters, eventStartPublisher, eventAsyncService);
    }

    @Test
    void invalid_InvalidOwnerId() {
        EventDto eventDto = createEventDto();

        Exception exception = assertThrows(UserNotFoundException.class, () -> eventService.create(eventDto));
        assertEquals("User not found. ID: 1", exception.getMessage());
    }

    @Test
    void invalid_userNotContainsSkills() {
        EventDto eventDto = createEventDto();
        eventDto.setRelatedSkills(List.of(new SkillDto(3L, "C"), new SkillDto(2L, "B")));

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser()));

        Exception exception = assertThrows(DataValidException.class, () -> eventService.create(eventDto));
        assertEquals("User has no related skills. Id: 1", exception.getMessage());

        verify(userRepository).findById(1L);
    }

    @Test
    void create_ShouldReturnEventDto() {
        Event event = createEvent();
        User user = createUser();
        Long userId = 1L;
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);

        EventDto eventDto = createEventDto();
        EventDto createdEventDto = eventService.create(eventDto);

        assertEquals(eventDto.getTitle(), createdEventDto.getTitle());
        assertEquals(eventDto.getStartDate(), createdEventDto.getStartDate());
        assertEquals(eventDto.getOwnerId(), createdEventDto.getOwnerId());
        assertThat(eventDto.getRelatedSkills()).containsExactlyInAnyOrderElementsOf(createdEventDto.getRelatedSkills());

        verify(userRepository).findById(userId);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void get_invalidEventId() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> eventService.get(99L));
        assertEquals("Event not found", exception.getMessage());
    }

    @Test
    void get_ShouldReturnEventDto() {
        Event event = createEvent();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Long eventId = 1L;
        EventDto foundEventDto = eventService.get(eventId);

        assertNotNull(foundEventDto);
        assertEquals(event.getId(), foundEventDto.getId());

        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    public void deleteEvent_EventExists() {
        Long eventId = 1L;
        when(eventRepository.existsById(eventId)).thenReturn(true);

        boolean result = eventService.deleteEvent(eventId);

        assertTrue(result);

        verify(eventRepository, times(1)).deleteById(eventId);
        verify(eventRepository, times(1)).existsById(eventId);
    }

    @Test
    public void deleteEvent_EventNotExist() {
        Long eventId = 1L;
        when(eventRepository.existsById(eventId)).thenReturn(false);

        boolean result = eventService.deleteEvent(eventId);

        assertFalse(result);

        verify(eventRepository, times(1)).existsById(eventId);
    }

    @Test
    void getEventsByFilter_IdTitleFilter() {
        EventFilterDto filter = new EventFilterDto();
        filter.setId(1L);
        filter.setTitlePattern("^[a-zA-Z]+$");

        when(eventRepository.findAll()).thenReturn(createEventList());

        List<EventDto> result = eventService.getEventsByFilter(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void getEventsByFilter_DateFilter() {
        EventFilterDto filter = new EventFilterDto();
        filter.setLaterThanStartDate(LocalDate.of(2019, 6, 1).atStartOfDay());
        filter.setEarlierThanEndDate(LocalDate.of(2022, 7, 20).atStartOfDay());

        when(eventRepository.findAll()).thenReturn(createEventList());

        List<EventDto> result = eventService.getEventsByFilter(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    void getEventsByFilter_maxAttendeesFilter() {
        EventFilterDto filter = new EventFilterDto();
        filter.setLessThanMaxAttendees(2);

        when(eventRepository.findAll()).thenReturn(createEventList());

        List<EventDto> result = eventService.getEventsByFilter(filter);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertNotEquals(3L, result.get(0).getId());
        assertNotEquals(3L, result.get(1).getId());
    }

    @Test
    void getOwnedEvents_WithExistingUserId() {
        long userId = 1L;
        List<Event> events = createEvents();
        when(eventRepository.findAllByUserId(userId)).thenReturn(events);

        List<EventDto> eventDtos = eventService.getOwnedEvents(userId);

        assertEquals(events.size(), eventDtos.size());
    }

    @Test
    void getOwnedEvents_WithNonExistingUserId() {
        long userId = 1L;
        when(eventRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        List<EventDto> eventDtos = eventService.getOwnedEvents(userId);

        assertTrue(eventDtos.isEmpty());
    }

    @Test
    void getParticipatedEvents_ShouldReturnEventDtos() {
        long userId = 1L;
        List<Event> participatedEvents = createEvents();

        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(participatedEvents);

        List<EventDto> eventDtos = eventService.getParticipatedEvents(userId);

        assertNotNull(eventDtos);
        assertEquals(participatedEvents.size(), eventDtos.size());
    }

    @Test
    void updateEvent_ValidEventDto() {
        Event event = createEvent();
        EventDto eventDto = createEventDto();
        eventDto.setTitle("Event2");
        eventDto.setStartDate(LocalDate.of(2020, 3, 1).atStartOfDay());
        eventDto.setRelatedSkills(List.of(new SkillDto(1L, "A")));

        when(eventRepository.findById(eventDto.getId())).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(eventMapper.toEntity(eventDto));
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser()));
        when(userRepository.existsById(anyLong())).thenReturn(true);

        EventDto updatedEvent = eventService.updateEvent(eventDto);

        assertEquals(eventDto.getTitle(), updatedEvent.getTitle());
        assertEquals(eventDto.getStartDate(), updatedEvent.getStartDate());
        assertEquals(eventDto.getEndDate(), updatedEvent.getEndDate());
        assertEquals(eventDto.getDescription(), updatedEvent.getDescription());
        assertEquals(eventDto.getLocation(), updatedEvent.getLocation());
        assertEquals(eventDto.getMaxAttendees(), updatedEvent.getMaxAttendees());
        assertEquals(eventDto.getRelatedSkills(), updatedEvent.getRelatedSkills());
    }

    @Test
    void updateEvent_EventNotFound() {
        EventDto eventDto = createEventDto();
        when(eventRepository.findById(eventDto.getId())).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser()));
        when(userRepository.existsById(anyLong())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> eventService.updateEvent(eventDto));
        assertEquals("Event not found. ID: 1", exception.getMessage());
    }

    @Test
    void startEventTest() {
        User user = User.builder().id(1L).build();
        Event event = Event.builder().id(2L)
                .attendees(new ArrayList<>(List.of(user, user, user)))
                .status(EventStatus.PLANNED)
                .build();

        when(eventRepository.findById(2L)).thenReturn(Optional.of(event));

        EventStartDto result = eventService.startEvent(2L);

        assertEquals(3, result.getUserIds().size());
        assertEquals(2, result.getEventId());

        verify(eventStartPublisher).publishMessage(result);
    }

    private EventDto createEventDto() {
        EventDto eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setTitle("Test Event");
        eventDto.setStartDate(LocalDate.of(2020, 1, 1).atStartOfDay());
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(List.of(new SkillDto(1L, "A"), new SkillDto(2L, "B")));
        return eventDto;
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setSkills(createSkills());
        return user;
    }

    private Event createEvent() {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setStartDate(LocalDate.of(2020, 1, 1).atStartOfDay());
        event.setOwner(createUser());
        event.setRelatedSkills(createSkills());
        return event;
    }

    private List<Skill> createSkills() {
        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("A");
        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("B");
        return List.of(skill1, skill2);
    }

    private List<Event> createEventList() {
        Event event1 = new Event();
        event1.setId(1L);
        event1.setTitle("Title");
        Event event2 = new Event();
        event2.setId(2L);
        event2.setStartDate(LocalDate.of(2020, 1, 1).atStartOfDay());
        event2.setEndDate(LocalDate.of(2021, 1, 1).atStartOfDay());
        Event event3 = new Event();
        event3.setId(3L);
        event3.setMaxAttendees(5);
        return List.of(event1, event2, event3);
    }

    private List<Event> createEvents() {
        List<Event> events = new ArrayList<>();
        User user = createUser();
        for (int i = 1; i <= 5; i++) {
            Event event = new Event();
            event.setId(i);
            event.setTitle("Test Event " + i);
            event.setStartDate(LocalDate.of(2020, 1, i).atStartOfDay());
            event.setOwner(user);
            event.setRelatedSkills(createSkills());

            events.add(event);
        }
        return events;
    }

    @Test
    void testDeletePastEvents() {
        List<Event> pastEvents = new ArrayList<>();
        pastEvents.add(Event.builder().description("Event 1").endDate(LocalDateTime.now().withNano(0)).build());
        pastEvents.add(Event.builder().description("Event 2").endDate(LocalDateTime.now().withNano(0)).build());

        LocalDateTime date = LocalDateTime.now().withNano(0);
        when(eventRepository.findAllByCreatedAtBefore(date)).thenReturn(pastEvents);

        eventService.deletePastEvents(10);

        verify(eventRepository).findAllByCreatedAtBefore(date);
        verify(eventAsyncService).clearEventsPartition(pastEvents);
    }

}