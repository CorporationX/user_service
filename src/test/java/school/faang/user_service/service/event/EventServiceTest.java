package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventWithSubscribersDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filters.EventFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private List<EventFilter> eventFilters;
    @InjectMocks
    private EventService eventService;
    @Mock
    private User user;
    private EventDto eventDto;
    private Event event;
    private List<Skill> relatedSkills;

    @BeforeEach
    void setUp() {
        eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkillsIds(Arrays.asList(1L, 2L));

        user = mock(User.class);
        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("Java");

        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("C++");

        relatedSkills = Arrays.asList(skill1, skill2);

        event = new Event();
        event.setId(1L);
        event.setOwner(user);
        event.setRelatedSkills(relatedSkills);
        EventFilter mockFilter = mock(EventFilter.class);

        lenient().when(mockFilter.isApplicable(any(EventFilterDto.class))).thenReturn(true);
        lenient().doNothing().when(mockFilter).apply(any(), any());

        eventFilters = new ArrayList<>();
        eventFilters.add(mockFilter);

        lenient().when(eventMapper.toEvent(eventDto)).thenReturn(event);
        lenient().when(eventMapper.toDto(event)).thenReturn(eventDto);
    }

    @Test
    void create_ShouldCreateEvent() {
        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        when(user.getSkills()).thenReturn(relatedSkills);
        when(skillRepository.findAllById(eventDto.getRelatedSkillsIds())).thenReturn(relatedSkills);

        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventDto result = eventService.create(eventDto);

        verify(skillRepository).findAllById(eventDto.getRelatedSkillsIds());
        verify(userRepository).findById(eventDto.getOwnerId());
        verify(eventRepository).save(event);

        assertEquals(eventDto, result);
    }

    @Test
    void getEvent_ShouldReturnEventDto() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

        EventDto result = eventService.getEvent(1L);

        assertNotNull(result);
        assertEquals(eventDto, result);
        verify(eventRepository).findById(1L);
    }

    @Test
    void getEventsByFilter_ShouldReturnFilteredEvents() {
        List<Event> events = List.of(new Event(), new Event());
        when(eventRepository.findAll()).thenReturn(events);

        when(eventMapper.toFilterDto(anyList())).thenReturn(List.of(new EventDto(), new EventDto()));

        List<EventDto> result = eventService.getEventsByFilter(new EventFilterDto());

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void updateEvent_ShouldUpdateEvent() {
        doReturn(Optional.of(event)).when(eventRepository).findById(anyLong());
        doReturn(Optional.of(user)).when(userRepository).findById(anyLong());
        doReturn(relatedSkills).when(skillRepository).findAllById(anyList());
        doReturn(relatedSkills).when(user).getSkills();
        doReturn(event).when(eventRepository).save(any(Event.class));

        EventWithSubscribersDto eventWithSubscribersDto = new EventWithSubscribersDto();
        doReturn(eventWithSubscribersDto).when(eventMapper).toEventWithSubscribersDto(any(Event.class));

        EventWithSubscribersDto result = eventService.updateEvent(eventDto);
        result.setSubscribersCount(3);

        assertNotNull(result);
        assertEquals(3, result.getSubscribersCount());
        verify(eventRepository).save(event);
    }


    @Test
    void deleteEvent_ShouldDeleteEvent() {
        when(eventRepository.existsById(anyLong())).thenReturn(true);

        eventService.deleteEvent(1L);

        verify(eventRepository).deleteById(1L);
    }

    @Test
    void getOwnedEvents_ShouldReturnOwnedEvents() {
        List<Event> events = List.of(new Event(), new Event());
        when(eventRepository.findAllByUserId(anyLong())).thenReturn(events);
        when(eventMapper.toDto(anyList())).thenReturn(List.of(new EventDto(), new EventDto()));

        List<EventDto> result = eventService.getOwnedEvents(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getParticipatedEvents_ShouldReturnParticipatedEvents() {
        List<Event> events = List.of(new Event(), new Event());
        when(eventRepository.findParticipatedEventsByUserId(anyLong())).thenReturn(events);
        when(eventMapper.toDto(anyList())).thenReturn(List.of(new EventDto(), new EventDto()));

        List<EventDto> result = eventService.getParticipatedEvents(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}