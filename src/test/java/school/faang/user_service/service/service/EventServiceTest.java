package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filters.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.event.EventValidator;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventMapper eventMapper;
    @Mock
    private SkillMapper skillMapper;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventFilter filter;
    @Mock
    private EventValidator eventValidator;
    @Mock
    private List<EventFilter> eventFilters;

    @InjectMocks
    private EventService eventService;
    ArgumentCaptor eventCaptor = ArgumentCaptor.forClass(Event.class);
    private EventDto firstEventDto = new EventDto();
    private EventDto secondEventDto = new EventDto();
    private Event firstEvent = new Event();
    private Event secondEvent = new Event();
    private User owner = new User();
    private Skill firstSkill = new Skill();
    private Skill secondSkill = new Skill();
    private SkillDto firstSkillDto = new SkillDto();
    private SkillDto secondSkillDto = new SkillDto();
    private List<SkillDto> skillDtoList = new ArrayList<>();
    private List<Event> eventList = List.of(firstEvent, secondEvent);
    private List<EventDto> eventDtoList = List.of(firstEventDto, secondEventDto);

    @BeforeEach
    public void setUp() {
        owner.setId(1L);
        owner.setSkills(List.of(firstSkill));

        firstEventDto.setId(1L);
        firstEventDto.setOwnerId(1L);
        firstEventDto.setTitle("first");
        firstEventDto.setRelatedSkills(skillDtoList);

        secondEventDto.setId(2L);
        secondEventDto.setOwnerId(2L);
        secondEventDto.setTitle("second");

        firstEvent.setId(1L);
        firstEvent.setOwner(owner);
        firstEvent.setTitle("first");

        secondEvent.setId(2L);
        secondEvent.setOwner(owner);
        secondEvent.setTitle("second");

        firstSkill.setId(1L);
        firstSkill.setUsers(List.of(owner));

        secondSkill.setId(2L);
        secondSkill.setUsers(List.of(owner));

        firstSkillDto.setId(1L);
        firstSkillDto.setUserIds(List.of(owner.getId()));
        secondSkillDto.setUserIds(List.of(owner.getId()));

        skillDtoList = List.of(firstSkillDto, secondSkillDto);
    }

    @Test
    public void testCreate_ValidEventDto() {
        when(eventMapper.toEntity(firstEventDto)).thenReturn(new Event());
        when(eventRepository.save(any())).thenReturn(new Event());
        when(eventMapper.toDto((Event) eventCaptor.capture())).thenReturn(firstEventDto);
        EventDto createdEventDto = eventService.create(firstEventDto);
        assertNotNull(createdEventDto);
    }

    @Test
    public void testGetEventById_EventExists() {
        firstEventDto.setId(1L);
        Event event = new Event();
        event.setId(1L);
        when(eventRepository.findById(firstEventDto.getId())).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(firstEventDto);
        EventDto result = eventService.getEventById(firstEventDto.getId());
        assertNotNull(result);
        assertEquals(firstEventDto.getId(), result.getId());
    }

    @Test
    public void testGetEventsByFilter() {
        EventFilterDto filters = new EventFilterDto();
        EventFilter firstEventFilter = mock(EventFilter.class);
        EventFilter secondEventFilter = mock(EventFilter.class);

        when(eventFilters.stream()).thenReturn(Stream.of(firstEventFilter, secondEventFilter));

        when(firstEventFilter.isApplicable(filters)).thenReturn(true);
        when(secondEventFilter.isApplicable(filters)).thenReturn(false);
        when(firstEventFilter.apply(any(), eq(filters))).thenReturn(Stream.of(firstEvent));
        when(eventMapper.toDto(firstEvent)).thenReturn(firstEventDto);
        List<EventDto> result = eventService.getEventsByFilter(filters);
        assertEquals(1, result.size());
        assertEquals(firstEventDto, result.get(0));

        verify(eventRepository, times(1)).findAll();
        verify(firstEventFilter, times(1)).isApplicable(filters);
        verify(secondEventFilter, times(1)).isApplicable(filters);
        verify(firstEventFilter, times(1)).apply(any(), eq(filters));
        verify(secondEventFilter, times(0)).apply(any(), eq(filters));
        verify(eventMapper, times(1)).toDto(firstEvent);
    }

    @Test
    public void testDeleteEvent() {
        Long eventId = 1L;
        eventService.deleteEvent(eventId);
        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    public void testUpdateEvent_ValidEvent() {
        when(eventRepository.findById(firstEventDto.getId())).thenReturn(Optional.of(firstEvent));
        EventDto updatedEvent = eventService.updateEvent(firstEventDto.getId(), firstEventDto);
    }

    @Test
    public void testGetOwnedEvents() {
        Long userId = 1L;
        List<Event> events = new ArrayList<>();
        List<EventDto> expectedEventDto = new ArrayList<>();
        when(eventRepository.findAllByUserId(userId)).thenReturn(events);
        when(eventMapper.toDtoList(events)).thenReturn(expectedEventDto);
        List<EventDto> actualEventDto = eventService.getOwnedEvents(userId);
        verify(eventRepository, times(1)).findAllByUserId(userId);
        assertEquals(expectedEventDto, actualEventDto);
    }

    @Test
    public void testGetParticipatedEvents() {
        Long userId = 1L;
        List<Event> events = new ArrayList<>();
        List<EventDto> expectedEventDto = new ArrayList<>();
        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(events);
        when(eventMapper.toDtoList(events)).thenReturn(expectedEventDto);
        List actualEventDto = eventService.getParticipatedEvents(userId);
        verify(eventRepository, times(1)).findParticipatedEventsByUserId(userId);
        assertEquals(expectedEventDto, actualEventDto);
    }
}