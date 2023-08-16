package school.faang.user_service.service.eventTest;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.invalidFieldException.DataValidationException;
import school.faang.user_service.exception.notFoundExceptions.event.EventNotFoundException;
import school.faang.user_service.filter.event.Filter;
import school.faang.user_service.filter.event.LocationFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.EventMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private EventMapper eventMapper;

    List<Filter<Event, EventFilterDto>> filters;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventService eventService;

    private EventDto eventDto;
    private EventFilterDto filterDto;
    private Event event;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        var now = LocalDateTime.now();
        eventDto = new EventDto(0L, "title", now, now.plusDays(3), 0L, "0", new ArrayList<>(), new ArrayList<>(), "location", EventType.WEBINAR, EventStatus.PLANNED, -1);
        filterDto = new EventFilterDto("title", now.plusHours(1), now.plusDays(10), 0L, List.of(), "location", 10, false);
        event = Event.builder()
                .id(1)
                .attendees(new ArrayList<>())
                .build();

        Filter<Event, EventFilterDto> filter = Mockito.mock(Filter.class);
        filters = List.of(filter);
        eventService = new EventService(eventRepository, skillRepository, userRepository, eventMapper, filters);

        Mockito.when(skillRepository.findAllByUserId(eventDto.getOwnerId()))
                .thenReturn(List.of(
                        Skill.builder().id(1).build(),
                        Skill.builder().id(2).build()
                ));
        Mockito.when(eventMapper.toEntity(Mockito.any())).thenReturn(event);

        Mockito.when(eventRepository.findAll()).thenReturn(List.of(event));
        Mockito.when(eventMapper.toDto(Mockito.any(Event.class))).thenReturn(eventDto);
    }

    @Test
    void testSkillIsValid() {
        eventDto.setRelatedSkills(List.of(new SkillDto(1L,"1"), new SkillDto(2L,"2")));
        eventService.create(eventDto);

        Mockito.verify(eventMapper, Mockito.times(1)).toDto(Mockito.any());
        Mockito.verify(eventRepository).save(Mockito.any());
    }

    @Test
    void testSkillsAreInvalid() {
        eventDto.setRelatedSkills(List.of(new SkillDto(3L,"3"), new SkillDto(4L,"4")));

        Assert.assertThrows(
                DataValidationException.class,
                () -> eventService.create(eventDto)
        );
    }

    @Test
    void testEntityIsNull() {
        Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(
                Optional.empty()
        );

        Assert.assertThrows(
                EventNotFoundException.class,
                () -> eventService.getEvent(5)
        );
    }

    @Test
    void testEntityIsNotNull() {
        var event = Event.builder().id(0).build();
        Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(
                Optional.of(event)
        );
        Mockito.when(eventMapper.toDto(Mockito.any())).thenReturn(eventDto);

        eventService.getEvent(Mockito.anyLong());
        Mockito.verify(eventMapper, Mockito.times(1)).toDto(event);
    }

    @Test
    void testReceivingFilteredEvents() {
        event = getEventExample();
        List<Event> events = List.of(event);
        Mockito.when(eventRepository.findAll()).thenReturn(events);
        Mockito.when(eventMapper.toDto(Mockito.any(Event.class))).thenReturn(eventDto);

        var result = eventService.getEventsByFilter(filterDto);
        Assertions.assertEquals("title", result.get(0).getTitle());
    }

    @Test
    void testReceivingWrongLocationFilter() {
        event = getEventExample();
        event.setLocation("anotherLocation");

        Mockito.when(filters.get(0).isApplicable(Mockito.any())).thenReturn(true);
        Mockito.when(filters.get(0).applyFilter(Stream.of(event), filterDto)).thenReturn(Stream.empty());
        var res = eventService.getEventsByFilter(filterDto).size();
        Assertions.assertEquals(0, res);

    }

    @Test
    void deletingEventTest() {
        Mockito.when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));
        eventService.deleteEvent(1);
        Mockito.verify(eventRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void testUpdatingEventIsInvalid() {
        eventDto.setRelatedSkills(List.of(new SkillDto(3L,"3"), new SkillDto(4L,"4")));

        Assert.assertThrows(
                DataValidationException.class,
                () -> eventService.updateEvent(eventDto)
        );
    }

    @Test
    void testUpdatingEventIsValid() {
        eventDto.setRelatedSkills(List.of(new SkillDto(1L,"1"), new SkillDto(2L,"2")));
        eventService.updateEvent(eventDto);

        Mockito.verify(eventMapper, Mockito.times(1)).toEntity(eventDto);
    }

    @Test
    void testReceivingOwnedEvents() {
        long eventId = 1;
        eventService.getOwnedEvents(eventId);

        Mockito.verify(eventRepository, Mockito.times(1)).findAllByUserId(eventId);
    }

    @Test
    void testGettingParticipatedEvents() {
        long eventId = 1;
        eventService.getParticipatedEvents(eventId);

        Mockito.verify(eventRepository, Mockito.times(1)).findParticipatedEventsByUserId(eventId);
    }

    private Event getEventExample() {
        return Event.builder()
                .id(0)
                .title("title")
                .description("description")
                .startDate(LocalDateTime.now().plusHours(2))
                .endDate(LocalDateTime.now().plusDays(1))
                .location("location")
                .maxAttendees(100)
                .owner(User.builder().id(0).build())
                .relatedSkills(new ArrayList<>())
                .type(null)
                .status(null)
                .build();
    }
}
