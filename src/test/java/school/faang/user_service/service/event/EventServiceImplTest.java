package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.entity.Skill;
import school.faang.user_service.dto.entity.User;
import school.faang.user_service.dto.entity.event.Event;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filter.EventDescriptionFilter;
import school.faang.user_service.service.event.filter.EventTitleFilter;
import school.faang.user_service.service.event.impl.EventServiceImpl;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.adapter.user.UserRepositoryAdapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {
    @InjectMocks
    private EventServiceImpl eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private SkillService skillService;

    @Mock
    private UserRepositoryAdapter userRepositoryAdapter;

    @Mock
    private EventTitleFilter eventTitleFilter;

    @Mock
    private EventDescriptionFilter eventDescriptionFilter;

    @Mock
    private EventMapper eventMapper;

    @Captor
    private ArgumentCaptor<Event> eventCaptor;

    @Captor
    private ArgumentCaptor<Long> eventIdCaptor;
    @Captor
    private ArgumentCaptor<List<Event>> eventsCaptor;

    private static final long OWNER_ID = 1L;
    private static final long EVENT_ID = 1L;
    private static final List<Long> SKILL_IDS = List.of(1L, 2L);
    private static final Skill FIRST_SKILL = Skill.builder().id(1L).build();
    private static final Skill SECOND_SKILL = Skill.builder().id(2L).build();
    private static final List<Event> EVENTS = List.of(new Event(), new Event());
    private static final List<EventDto> EVENT_DTOS = List.of(new EventDto(), new EventDto());

    @BeforeEach
    void setUp() {
        eventService = new EventServiceImpl(
                eventRepository,
                skillService,
                userRepositoryAdapter,
                eventMapper,
                List.of(eventTitleFilter, eventDescriptionFilter));
    }

    @Test
    void testCreate_Success() {
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(OWNER_ID);
        eventDto.setRelatedSkillIds(SKILL_IDS);

        User user = new User();
        user.setId(OWNER_ID);

        Event event = new Event();
        Event savedEvent = new Event();
        EventDto savedEventDto = new EventDto();

        when(eventMapper.toEntity(eventDto)).thenReturn(event);
        when(userRepositoryAdapter.getUserById(OWNER_ID)).thenReturn(user);
        when(skillService.getSkillListBySkillIds(SKILL_IDS)).thenReturn(List.of(FIRST_SKILL, SECOND_SKILL));
        when(eventRepository.save(event)).thenReturn(savedEvent);
        when(eventMapper.toDto(savedEvent)).thenReturn(savedEventDto);

        EventDto result = eventService.create(eventDto);

        verify(eventMapper, times(1)).toEntity(eventDto);
        verify(userRepositoryAdapter, times(1)).getUserById(OWNER_ID);
        verify(skillService, times(1)).getSkillListBySkillIds(SKILL_IDS);
        verify(eventRepository, times(1)).save(eventCaptor.capture());
        verify(eventMapper, times(1)).toDto(savedEvent);
        assertEquals(savedEventDto, result);
        Event capturedEvent = eventCaptor.getValue();
        assertEquals(user, capturedEvent.getOwner());
        assertEquals(2, capturedEvent.getRelatedSkills().size());
    }

    @Test
    void testGetEvent_Success() {
        Event event = new Event();
        event.setId(EVENT_ID);

        EventDto eventDto = new EventDto();

        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        EventDto result = eventService.getEvent(EVENT_ID);

        assertNotNull(result);
        assertEquals(eventDto, result);
        verify(eventRepository, times(1)).findById(eventIdCaptor.capture());
        assertEquals(EVENT_ID, eventIdCaptor.getValue());
        verify(eventMapper, times(1)).toDto(event);
    }

    @Test
    void testGetEvent_NotFound() {
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> eventService.getEvent(EVENT_ID));

        assertEquals("Событие по id: 1 не найдено!", exception.getMessage());
        verify(eventRepository, times(1)).findById(eventIdCaptor.capture());
        assertEquals(EVENT_ID, eventIdCaptor.getValue());
    }

    @Test
    void testDeleteEvent_Success() {
        doNothing().when(eventRepository).deleteById(EVENT_ID);
        eventService.deleteEvent(EVENT_ID);

        verify(eventRepository, times(1)).deleteById(eventIdCaptor.capture());
        assertEquals(EVENT_ID, eventIdCaptor.getValue());
    }

    @Test
    void testUpdateEvent_Success() {
        EventDto eventDto = new EventDto();
        eventDto.setRelatedSkillIds(SKILL_IDS);

        Event event = new Event();
        Event updatedEvent = new Event();
        EventDto updatedEventDto = new EventDto();

        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(skillService.getSkillListBySkillIds(SKILL_IDS)).thenReturn(List.of(FIRST_SKILL, SECOND_SKILL));
        when(eventRepository.save(event)).thenReturn(updatedEvent);
        when(eventMapper.toDto(updatedEvent)).thenReturn(updatedEventDto);

        EventDto result = eventService.updateEvent(EVENT_ID, eventDto);

        verify(eventMapper, times(1)).update(event, eventDto);
        verify(skillService, times(1)).getSkillListBySkillIds(SKILL_IDS);
        verify(eventRepository, times(1)).save(eventCaptor.capture());
        verify(eventMapper, times(1)).toDto(updatedEvent);
        assertEquals(updatedEventDto, result);
        assertEquals(SKILL_IDS, eventCaptor.getValue().getRelatedSkills().stream().map(Skill::getId).toList());
    }

    @Test
    void testUpdateEvent_NotFound() {
        EventDto eventDto = new EventDto();

        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> eventService.updateEvent(EVENT_ID, eventDto));

        assertEquals("Событие по id: 1 не найдено!", exception.getMessage());
        verify(eventRepository, times(1)).findById(EVENT_ID);
    }

    @Test
    void testGetOwnedEvents_Success() {
        when(eventRepository.findAllByUserId(OWNER_ID)).thenReturn(EVENTS);
        when(eventMapper.toDto(EVENTS)).thenReturn(EVENT_DTOS);

        List<EventDto> result = eventService.getOwnedEvents(OWNER_ID);

        assertNotNull(result);
        assertEquals(EVENT_DTOS, result);
        verify(eventRepository, times(1)).findAllByUserId(OWNER_ID);
        verify(eventMapper, times(1)).toDto(EVENTS);
    }

    @Test
    void testGetParticipatedEvents_Success() {
        when(eventRepository.findParticipatedEventsByUserId(OWNER_ID)).thenReturn(EVENTS);
        when(eventMapper.toDto(EVENTS)).thenReturn(EVENT_DTOS);

        List<EventDto> result = eventService.getParticipatedEvents(OWNER_ID);

        assertNotNull(result);
        assertEquals(EVENT_DTOS, result);
        verify(eventRepository, times(1)).findParticipatedEventsByUserId(OWNER_ID);
        verify(eventMapper, times(1)).toDto(EVENTS);
    }

    @Test
    void testGetEventsByFilter_Success() {
        EventFilterDto filters = new EventFilterDto();
        filters.setTitle("Test Title");
        filters.setDescription("Test Description");

        Event firstEvent = Event.builder()
                .title("Test Title")
                .description("Test Description")
                .build();

        Event secondEvent = Event.builder()
                .title("Another Title")
                .description("Another Description")
                .build();

        List<Event> events = List.of(firstEvent, secondEvent);

        when(eventRepository.findAll()).thenReturn(events);
        doReturn(EVENT_DTOS).when(eventMapper).toDto(anyList());

        when(eventTitleFilter.isApplicable(filters)).thenReturn(true);
        when(eventTitleFilter.apply(any(Stream.class), eq(filters))).thenAnswer(invocation -> {
            Stream<Event> eventStream = invocation.getArgument(0);
            return eventStream.filter(event -> event.getTitle().contains(filters.getTitle()));
        });
        when(eventDescriptionFilter.isApplicable(filters)).thenReturn(true);
        when(eventDescriptionFilter.apply(any(Stream.class), eq(filters))).thenAnswer(invocation -> {
            Stream<Event> eventStream = invocation.getArgument(0);
            return eventStream.filter(event -> event.getDescription().contains(filters.getDescription()));
        });

        List<EventDto> result = eventService.getEventsByFilter(filters);

        assertNotNull(result);
        assertEquals(EVENT_DTOS, result);
        verify(eventRepository).findAll();
        verify(eventMapper).toDto(eventsCaptor.capture());
        verify(eventTitleFilter).isApplicable(filters);
        verify(eventTitleFilter).apply(any(Stream.class), eq(filters));
        verify(eventDescriptionFilter).isApplicable(filters);
        verify(eventDescriptionFilter).apply(any(Stream.class), eq(filters));
        List<Event> capturedEvents = eventsCaptor.getValue();
        assertEquals(1, capturedEvents.size());
    }
}
