package school.faang.user_service.service.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.Exceptions;
import school.faang.user_service.mapper.EventMapperImpl;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.filter.EventFilter;
import school.faang.user_service.service.filter.EventTitleFilter;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validation.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private SkillService skillService;
    @Mock
    private Validator validator;
    @Spy
    private EventMapperImpl eventMapper;
    @Spy
    private Exceptions exceptions;
    private static List<EventFilter> filters;

    private static Event firstEventEntity;
    private static EventDto eventDto;
    private static EventDto thirdEventDto;
    private static List<Event> eventEntityList;
    private static final LocalDateTime dateTime = LocalDateTime.now();

    @BeforeAll
    public static void init() {
        firstEventEntity = createEvent(1L, "title");
        Event secondEventEntity = createEvent(2L, "title");
        Event thirdEventEntity = createEvent(3L, "title_check");

        eventDto = EventDto.builder().
                id(1L).
                title("title").
                description("description").
                startDate(dateTime).
                endDate(dateTime.plusDays(12)).
                location("location").
                maxAttendees(2).
                ownerId(1L).
                relatedSkills(null)
                .build();

        thirdEventDto = EventDto.builder().
                id(3L).
                title("title_check").
                description("description").
                startDate(dateTime).
                endDate(dateTime.plusDays(12)).
                location("location").
                maxAttendees(2).
                ownerId(3L).
                relatedSkills(null)
                .build();


        eventEntityList = List.of(firstEventEntity, secondEventEntity, thirdEventEntity);

        EventFilter filterA = mock(EventTitleFilter.class);
        EventFilter filterB = mock(EventFilter.class);
        EventFilter filterC = mock(EventFilter.class);

        filters = List.of(filterA, filterB, filterC);
    }


    @Test
    public void testCreationEventDto() {
        System.out.println(eventDto);
        when(eventRepository.save(any())).thenReturn(firstEventEntity);

        System.out.println(eventDto);
        Assertions.assertEquals(eventDto, eventService.create(eventDto));

    }

    @Test
    public void testGetEvent() {
        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(firstEventEntity));

        assertEquals(eventDto, eventService.getEvent(1L));
    }

    @Test
    public void testGetEventIdDoesNotFind() {
        when(eventRepository.findById(any())).thenReturn(null);

        assertThrows(DataValidationException.class,
                () -> eventService.getEvent(1L));
    }

    @Test
    public void testGetEventsByFilter() {
        eventService.setFilters(filters);
        EventFilterDto eventFilterDto = EventFilterDto.builder().titlePattern("title_check")
                .build();

        when(eventRepository.findAll()).thenReturn(eventEntityList);
        when(filters.get(0).isApplicable(eventFilterDto)).thenReturn(true);
        when(filters.get(0).apply(anyList(), eq(eventFilterDto))).
                thenReturn(
                        Stream.<Event>builder().add(createEvent(3L, "title_check")).build());

        assertEquals(thirdEventDto, eventService.getEventsByFilter(eventFilterDto).get(0));
    }


    @Test
    public void testDeleteEvent() {
        Long id = 1L;
        when(validator.checkLongFieldIsNullAndZero(1L)).thenReturn(true);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(firstEventEntity));
        doNothing().when(eventRepository).deleteById(1L);

        eventService.deleteEvent(1L);
        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testUpdateEvent() {
        eventDto.setRelatedSkills(List.of());

        when(skillService.getUserSkills(anyLong())).thenReturn(List.of());
        when(eventMapper.toEntity(eventDto)).thenReturn(firstEventEntity);

        eventService.updateEvent(eventDto);

        verify(eventRepository).save(firstEventEntity);
    }

    @Test
    public void testGetOwnedEvents() {
        long id = 1L;
        when(eventRepository.findAllByUserId(id)).thenReturn(null);

        assertThrows(DataValidationException.class, () -> eventService.getOwnedEvents(id));
    }

    @Test
    public void testGetParticipatedEvents() {
        long id = 1L;
        when(eventRepository.findParticipatedEventsByUserId(id)).thenReturn(null);

        assertThrows(DataValidationException.class, () -> eventService.getParticipatedEvents(id));
    }


    private static Event createEvent(Long id, String title) {
        return Event.builder().
                id(id).
                title(title).
                description("description").
                startDate(dateTime).
                endDate(dateTime.plusDays(12)).
                location("location").
                maxAttendees(2).
                owner(User.builder().id(id).build()).
                relatedSkills(null)
                .build();
    }
}
