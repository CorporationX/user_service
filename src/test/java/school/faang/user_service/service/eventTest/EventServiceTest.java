package school.faang.user_service.service.eventTest;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private EventMapper eventMapper;
    @InjectMocks
    private EventService eventService;
    EventDto eventDto;
    EventFilterDto filterDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        var now = LocalDateTime.now();
        eventDto = new EventDto(0L, "title", now, now.plusDays(3), 0L, "0", new ArrayList<>(), "location", -1);
        filterDto = new EventFilterDto("title", now.plusHours(1), now.plusDays(10), 0L, List.of(), "location", 10);
        Mockito.when(skillRepository.findAllByUserId(eventDto.getOwnerId()))
                .thenReturn(List.of(
                        Skill.builder().id(1).build(),
                        Skill.builder().id(2).build()
                ));
    }

    @Test
    void testSkillIsValid() {
        eventDto.setRelatedSkills(List.of(new SkillDto(1), new SkillDto(2)));
        eventService.create(eventDto);
        Mockito.verify(eventMapper, Mockito.times(1)).toDto(Mockito.any());
    }

    @Test
    void testSkillsAreInvalid() {
        eventDto.setRelatedSkills(List.of(new SkillDto(3), new SkillDto(4)));
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
                NotFoundException.class,
                () -> eventService.getEvent(5)
        );
    }

    @Test
    void testEntityIsNotNull() {
        var event = Event.builder().id(0).build();
        Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(
                Optional.of(event)
        );
        eventService.getEvent(Mockito.anyLong());
        Mockito.verify(eventMapper, Mockito.times(1)).toDto(event);
    }

    @Test
    void testReceivingFilteredEvents() {
        Event event = getEventExample();

        List<Event> events = List.of(event);
        Mockito.when(eventRepository.findAll()).thenReturn(events);
        Mockito.when(eventMapper.toDto(Mockito.any(Event.class))).thenReturn(eventDto);
        var result = eventService.getEventsByFilter(filterDto);
        Assertions.assertEquals("title", result.get(0).getTitle());
    }

    @Test
    void testReceivingWrongLocationFilter() {
        Event event = getEventExample();
        event.setLocation("anotherLocation");
        List<Event> events = List.of(event);
        Mockito.when(eventRepository.findAll()).thenReturn(events);
        Mockito.when(eventMapper.toDto(Mockito.any(Event.class))).thenReturn(eventDto);
        Assertions.assertThrows(NotFoundException.class,
                () -> eventService.getEventsByFilter(filterDto));
    }

    @Test
    void testReceivingWrongTitleFilter() {
        Event event = getEventExample();
        event.setTitle("ERROR!");
        List<Event> events = List.of(event);
        Mockito.when(eventRepository.findAll()).thenReturn(events);
        Mockito.when(eventMapper.toDto(Mockito.any(Event.class))).thenReturn(eventDto);
        Assertions.assertThrows(NotFoundException.class,
                () -> eventService.getEventsByFilter(filterDto));
    }

    @Test
    void deletingEventTest() {
        eventService.deleteEvent(1);
        Mockito.verify(eventRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void testUpdatingEventIsInvalid() {
        eventDto.setRelatedSkills(List.of(new SkillDto(3), new SkillDto(4)));
        Assert.assertThrows(
                DataValidationException.class,
                () -> eventService.updateEvent(eventDto)
        );
    }

    @Test
    void testUpdatingEventIsValid() {
        eventDto.setRelatedSkills(List.of(new SkillDto(1), new SkillDto(2)));
        eventService.updateEvent(eventDto);
        Mockito.verify(eventMapper, Mockito.times(1)).toEntity(eventDto);
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
