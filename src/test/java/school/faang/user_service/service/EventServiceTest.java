package school.faang.user_service.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.scheduler.SchedulerConfig;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private SkillMapper skillMapper;

    @Mock
    private SchedulerConfig schedulerConfig;

    @InjectMocks
    private EventService eventService;

    private EventDto eventDto;
    private Event event;
    private User user;
    private SkillDto skillDto1;
    private SkillDto skillDto2;
    private Skill skill1;
    private Skill skill2;
    private List<Skill> skills;
    private List<SkillDto> skillsDtos;
    private List<Event> ownedEvents;
    private List<EventDto> ownedEventsDto;
    private EventFilter mockFilter;

    @BeforeEach
    public void setUp() {
        mockFilter = mock(EventFilter.class);
        eventService = new EventService(eventRepository, userRepository, userService, eventMapper, skillMapper, List.of(mockFilter), schedulerConfig);

        skillDto1 = SkillDto.builder().id(1L).title("Java").build();
        skillDto2 = SkillDto.builder().id(2L).title("Spring").build();

        skillsDtos = new ArrayList<>();
        skillsDtos.add(skillDto1);
        skillsDtos.add(skillDto2);

        skill1 = Skill.builder().id(1L).title("Java").build();
        skill2 = Skill.builder().id(2L).title("Spring").build();

        skills = new ArrayList<>();
        skills.add(skill1);
        skills.add(skill2);


        user = User.builder()
                .id(100L)
                .skills(skills)
                .build();


        event = Event.builder()
                .id(22L)
                .owner(user)
                .title("Java Conference 2024")
                .relatedSkills(skills)
                .build();


        eventDto = EventDto.builder()
                .id(22L)
                .ownerId(100L)
                .title("Java Conference 2024")
                .relatedSkills(skillsDtos)
                .build();

        ReflectionTestUtils.setField(eventService, "schedulerConfig", schedulerConfig);
        when(schedulerConfig.getBatchSize()).thenReturn(10);
        when(schedulerConfig.getThreadPoolSize()).thenReturn(5);
    }

    @Test
    void testCreateEventSaveCorrectEvent() {
        when(userService.getUserById(Long.valueOf(100L))).thenReturn(user);
        when(eventMapper.toEntity(eventDto)).thenReturn(event);
        when(skillMapper.toDtoList(user.getSkills()))
                .thenReturn(Arrays.asList(skillDto1, skillDto2));
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        EventDto result = eventService.create(eventDto);

        assertNotNull(result);
        assertEquals(eventDto, result);
        Mockito.verify(eventRepository, times(1)).save(event);
    }

    @Test
    void testCreateEventUserDoNotHaveRequiredSkills() {
        SkillDto sqlDto = SkillDto.builder().id(2L).title("SQL").build();
        User user2 = User.builder()
                .id(100L)
                .skills(
                        Arrays.asList(
                                Skill.builder().id(1L).title("Java").build(),
                                Skill.builder().id(2L).title("SQL").build()))
                .build();


        when(userService.getUserById(Long.valueOf(100L))).thenReturn(user2);
        when(skillMapper.toDtoList(user2.getSkills()))
                .thenReturn(Arrays.asList(skillDto1, sqlDto));

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void testGetEventSuccess() {
        when(eventRepository.findById(event.getId())).thenReturn(ofNullable(event));
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        EventDto result = eventService.getEvent(event.getId());

        assertNotNull(result);
        assertEquals(eventDto, result);
    }

    @Test
    void testGetEventWithNonExistentEvent() {
        when(eventRepository.findById(anyLong())).thenReturn(empty());

        assertThrows(DataValidationException.class,
                () -> eventService.getEventById(anyLong()));
    }

    @Test
    void testDeleteEvent_Success() {
        ownedEvents = new ArrayList<>();
        ownedEvents.add(event);
        user.setOwnedEvents(ownedEvents);
        List<User> attendees = new ArrayList<>();
        attendees.add(User.builder().id(2L).participatedEvents(ownedEvents).build());
        attendees.add(User.builder().id(3L).participatedEvents(ownedEvents).build());
        event.setAttendees(attendees);

        when(eventRepository.findById(22L)).thenReturn(of(event));

        eventService.deleteEvent(22L);

        verify(eventRepository, times(1)).deleteById(22L);
        verify(userRepository, times(1)).save(user);
        for (User attendee : attendees) {
            verify(userRepository, times(1)).save(attendee);
        }
    }

    @Test
    void testDeleteEventEventNotFound() {
        when(eventRepository.findById(22L)).thenReturn(empty());

        assertThrows(DataValidationException.class,
                () -> eventService.deleteEvent(22L));
    }

    @Test
    void testDeleteEventNoParticipantsOrOwner() {
        ownedEvents = new ArrayList<>();
        user.setOwnedEvents(ownedEvents);
        event.setAttendees(new ArrayList<>()); // Пустой список участников
        when(eventRepository.findById(event.getId())).thenReturn(of(event));

        eventService.deleteEvent(event.getId());

        verify(eventRepository, times(1)).deleteById(event.getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetOwnedEventsWithUserId() {
        ownedEventsDto = new ArrayList<>();
        ownedEventsDto.add(eventDto);
        ownedEvents = new ArrayList<>();
        ownedEvents.add(event);
        user.setOwnedEvents(ownedEvents);
        when(userService.getUserById(100L)).thenReturn(user);
        when(eventMapper.toDtoList(ownedEvents)).thenReturn(ownedEventsDto);

        List<EventDto> result = eventService.getOwnedEvents(100L);

        assertEquals(ownedEventsDto, result);
    }

    @Test
    void testGetOwnedWithoutUser() {
        when(userService.getUserById(anyLong())).
                thenThrow(DataValidationException.class);

        assertThrows(DataValidationException.class,
                () -> eventService.getOwnedEvents(anyLong()));
    }

    @Test
    void testGetOwnedEventsWhenNoOwnedEvents() {
        User user = mock(User.class);
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(user.getOwnedEvents()).thenReturn(Collections.emptyList());

        List<EventDto> result = eventService.getOwnedEvents(anyLong());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetParticipatedEventsWithExistingUser() {
        User user = mock(User.class);
        user.setId(1L);
        List<Event> participatedEvents = new ArrayList<>();
        participatedEvents.add(Event.builder().id(1L).build());
        participatedEvents.add(Event.builder().id(2L).build());
        List<EventDto> participatedEventsDto = new ArrayList<>();
        participatedEventsDto.add(EventDto.builder().id(1L).build());
        participatedEventsDto.add(EventDto.builder().id(2L).build());
        user.setParticipatedEvents(participatedEvents);

        when(userService.getUserById((long) user.getId())).thenReturn(user);
        when((user.getParticipatedEvents())).thenReturn(participatedEvents);
        when(eventMapper.toDtoList(participatedEvents)).thenReturn(participatedEventsDto);

        List<EventDto> result = eventService.getParticipatedEvents(user.getId());

        assertNotNull(result);
        assertEquals(participatedEventsDto, result);
    }

    @Test
    void testGetParticipatedEventsWithoutUser() {
        when(userService.getUserById(anyLong())).
                thenThrow(DataValidationException.class);

        assertThrows(DataValidationException.class,
                () -> eventService.getParticipatedEvents(anyLong()));
    }

    @Test
    void testGetParticipatedEventsWhenNoEvents() {
        User user = mock(User.class);
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(user.getParticipatedEvents()).thenReturn(Collections.emptyList());

        List<EventDto> result = eventService.getParticipatedEvents(anyLong());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetEventByIdWithExistingEvent() {
        when(eventRepository.findById(event.getId())).thenReturn(of(event));

        Event result = eventService.getEventById(event.getId());

        assertNotNull(result);
        assertEquals(event, result);
    }

    @Test
    void testGetEventByIdWithNonExistingEvent() {
        when(eventRepository.findById(anyLong())).thenThrow(DataValidationException.class);

        assertThrows(DataValidationException.class, () -> eventService.getEventById(anyLong()));
    }

    @Test
    void testUpdateEventWhenUserDoNotHaveSkills() {
        event.getRelatedSkills().add(Skill.builder().title("SQL").build());
        when(userService.getUserById(user.getId())).thenReturn(user);

        assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
        verify(eventRepository, times(0)).save(event);
    }

    @Test
    void testUpdateWithCorrectData() {
        EventDto updateEventDto = EventDto.builder()
                .id(event.getId())
                .title("SQL CONF")
                .ownerId(user.getId())
                .relatedSkills(skillsDtos)
                .build();

        Event updatedEvent = event;
        updatedEvent.setTitle("SQL CONF");

        when(userService.getUserById(user.getId())).thenReturn(user);
        when(eventRepository.findById(updateEventDto.id()))
                .thenReturn(Optional.ofNullable(event));
        when(skillMapper.toDtoList(user.getSkills()))
                .thenReturn(Arrays.asList(skillDto1, skillDto2));
        when(eventRepository.save(event)).thenReturn(updatedEvent);
        when(eventMapper.toDto(event)).thenReturn(updateEventDto);

        EventDto result = eventService.updateEvent(updateEventDto);

        assertNotNull(result);
        assertEquals(updateEventDto.title(), result.title());
        verify(eventRepository, times(1)).save(updatedEvent);
    }

    @Test
    void testEventsByFilterWithCorrectData() {
        EventFilterDto filterDto = EventFilterDto.builder()
                .title("Java Conference 2024")
                .ownerId(user.getId())
                .location("Moscow")
                .build();
        Event sqlEvent = Event.builder()
                .title("Sql Conf")
                .owner(new User())
                .location("Paris")
                .build();
        Event javaEvent = Event.builder()
                .title("Java Conference 2024")
                .owner(user)
                .location("Moscow")
                .build();
        EventDto javaEventDto = EventDto.builder()
                .title("Java Conference 2024")
                .ownerId(user.getId())
                .location("Moscow")
                .build();

        List<Event> allEvents = new ArrayList<>();
        allEvents.add(sqlEvent);
        allEvents.add(javaEvent);
        allEvents.add(event);
        List<Event> filteredEvents = new ArrayList<>();
        filteredEvents.add(javaEvent);
        filteredEvents.add(event);
        List<EventDto> eventDtosFilter = new ArrayList<>();
        eventDtosFilter.add(javaEventDto);
        eventDtosFilter.add(eventDto);
        when(eventRepository.findAll()).thenReturn(allEvents);
        when(mockFilter.isApplicable(filterDto)).thenReturn(true);
        lenient().when(mockFilter.apply(allEvents.stream(), filterDto)).thenReturn(filteredEvents.stream());
        when(eventMapper.toDtoList(anyList())).thenReturn(eventDtosFilter);

        List<EventDto> result = eventService.getEventsByFilter(filterDto);

        assertEquals(eventDtosFilter, result);
        assertEquals(2, result.size(),
                "Only one event should match the filter (the Java Conference 2024)");
        assertEquals("Java Conference 2024", result.get(0).title(),
                "The event title should match the filter title");
        assertEquals("Java Conference 2024", result.get(1).title(),
                "The event title should match the filter title");
        verify(eventRepository).findAll();
        verify(eventMapper).toDtoList(anyList());
    }

    @Test
    void testClearPastEvents() {
        // Given
        Event pastEvent1 = Event.builder().id(1L).endDate(LocalDateTime.now().minusDays(1)).build();
        Event pastEvent2 = Event.builder().id(2L).endDate(LocalDateTime.now().minusDays(2)).build();
        List<Event> pastEvents = Arrays.asList(pastEvent1, pastEvent2);

        when(eventRepository.findByEndDateBefore(any(LocalDateTime.class))).thenReturn(pastEvents);

        // When
        eventService.clearPastEvents();

        // Then
        verify(eventRepository, times(1)).findByEndDateBefore(any(LocalDateTime.class));
        verify(eventRepository, times(1)).deleteAll(pastEvents);
    }

    @Test
    void testClearPastEvents_NoEvents() {
        // Given
        when(eventRepository.findByEndDateBefore(any(LocalDateTime.class))).thenReturn(List.of());

        // When
        eventService.clearPastEvents();

        // Then
        verify(eventRepository, times(1)).findByEndDateBefore(any(LocalDateTime.class));
        verify(eventRepository, never()).deleteAll(anyList());
    }
}