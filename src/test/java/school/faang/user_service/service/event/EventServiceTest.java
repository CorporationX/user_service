package school.faang.user_service.service.event;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class EventServiceTest {

    @InjectMocks
    EventServiceImpl eventService;

    @Mock
    EventRepository eventRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    EventMapper eventMapper;

    @Mock
    List<EventFilter> eventFilters;

    @Mock
    SkillRepository skillRepository;

    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        eventDto = new EventDto(20L, "title", LocalDateTime.now(),
                LocalDateTime.now(), 1L, "description",
                List.of(), "location", 10, EventType.WEBINAR, EventStatus.COMPLETED);
    }

    @Test
    void testCreate() {
        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(new User()));
        when(skillRepository.findAllById(eventDto.getRelatedSkillsIds())).thenReturn(new ArrayList<>());
        when(eventMapper.toEntity(eventDto)).thenReturn(new Event());

        eventService.create(eventDto);

        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void testSKillNoCheck() {
        Assert.assertThrows(DataValidationException.class, () ->
            eventService.create(new EventDto(3L, null, LocalDateTime.now(),
                    LocalDateTime.now(), 1L, "description",
                    List.of(19L, 40L), "location", 10, EventType.WEBINAR, EventStatus.COMPLETED))
        );
    }

    @Test
    void testGetEvent() {
        when(eventRepository.findById(eventDto.getId())).thenReturn(Optional.of(new Event()));

        eventService.getEvent(eventDto.getId());

        verify(eventRepository).findById(eventDto.getId());
    }

    @Test
    void testGetEventsByFilter() {
        EventFilterDto eventFilterDto = new EventFilterDto();

        Event event1 = new Event();
        Event event2 = new Event();
        List<Event> events = Arrays.asList(event1, event2);

        when(eventRepository.findAll()).thenReturn(events);

        EventDto eventDto1 = new EventDto();
        EventDto eventDto2 = new EventDto();
        List<EventDto> eventDtos = Arrays.asList(eventDto1, eventDto2);

        when(eventMapper.toDtoList(any())).thenReturn(eventDtos);

        List<EventDto> result = eventService.getEventsByFilter(eventFilterDto);

        assertEquals(eventDtos, result);

        verify(eventRepository).findAll();
        verify(eventMapper).toDtoList(any());
    }

    @Test
    void testDeleteEvent() {
        eventService.deleteEvent(eventDto.getId());

        verify(eventRepository).deleteById(eventDto.getId());
    }

    @Test
    void testUpdateEvent() {
        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(new User()));
        when(skillRepository.findAllById(eventDto.getRelatedSkillsIds())).thenReturn(new ArrayList<>());
        when(eventMapper.toEntity(eventDto)).thenReturn(new Event());

        eventService.updateEvent(eventDto);

        verify(eventRepository).save(any(Event.class));
        }

    @Test
    void testGetOwnedEvents() {
        long userId = 0L;
        List<Event> events = List.of(new Event());
        List<EventDto> dtos = List.of(new EventDto());

        when(eventRepository.findAllByUserId(userId)).thenReturn(events);
        when(eventMapper.toDtoList(events)).thenReturn(dtos);

        List<EventDto> result = eventService.getOwnedEvents(userId);

        assertEquals(dtos, result);

        verify(eventRepository).findAllByUserId(userId);
    }

    @Test
    void testGetParticipatedEvents() {
        long userId = 1L;
        List<Event> events = List.of(new Event());
        List<EventDto> dtos = List.of(new EventDto());

        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(events);

        when(eventMapper.toDto(any(Event.class))).thenAnswer(invocation -> {
            Event event = invocation.getArgument(0);
            return new EventDto();
        });

        List<EventDto> result = eventService.getParticipatedEvents(userId);

        assertEquals(dtos, result);

        verify(eventRepository).findParticipatedEventsByUserId(userId);

        verify(eventMapper, Mockito.times(events.size())).toDto(any(Event.class));
    }

    @Test
    void testClearOutdatedEvents() {
        Event event = new Event();
        event.setId(1L);

        assertDoesNotThrow(() -> eventService.clearOutdatedEvents(List.of(event)));
    }
}