package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.executor.ExecutorsConfig;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filter.EventFilter;
import school.faang.user_service.validation.event.EventValidator;
import school.faang.user_service.validation.user.UserValidator;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    private EventRepository eventRepository;
    private EventMapperImpl eventMapper;
    private List<EventFilter> eventFilters;
    private EventValidator eventValidator;
    private UserValidator userValidator;
    private ExecutorsConfig executorsConfig;
    private EventService eventService;

    private EventFilterDto eventFilterDto;
    private EventFilter eventFilter;
    private Event event;
    private Event event1;
    private Event event2;
    private Event event3;
    private EventDto eventDto;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(3)
                .username("Valid username")
                .build();
        event = Event.builder()
                .id(4)
                .title("Valid event title")
                .description("Valid description")
                .startDate(LocalDateTime.of(2024, Month.JANUARY, 2, 12, 0))
                .endDate(LocalDateTime.now().plusYears(5))
                .location("Valid location")
                .maxAttendees(50)
                .owner(user)
                .relatedSkills(Collections.emptyList())
                .build();
        event1 = Event.builder()
                .endDate(LocalDateTime.now().minusDays(2))
                .build();
        event2 = Event.builder()
                .endDate(LocalDateTime.now().plusDays(1))
                .build();
        event3 = Event.builder()
                .endDate(LocalDateTime.now().minusDays(3))
                .build();
        eventDto = EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .ownerId(event.getOwner().getId())
                .description(event.getDescription())
                .relatedSkillsIds(Collections.emptyList())
                .location(event.getLocation())
                .maxAttendees(event.getMaxAttendees())
                .build();
        eventFilterDto = EventFilterDto.builder()
                .title("title")
                .build();

        eventRepository = mock(EventRepository.class);
        eventMapper = spy(EventMapperImpl.class);
        eventFilter = mock(EventFilter.class);
        eventFilters = List.of(eventFilter);
        eventValidator = mock(EventValidator.class);
        userValidator = mock(UserValidator.class);
        executorsConfig = mock(ExecutorsConfig.class);
        eventService = new EventService(eventRepository, eventMapper, eventFilters, eventValidator, userValidator, executorsConfig);
    }

    @Test
    public void testClearPastEvent() {
        List<Event> events = Arrays.asList(event1, event2, event3);
        ExecutorService mockExecutor = mock(ExecutorService.class);
        when(executorsConfig.executorService()).thenReturn(mockExecutor);
        when(eventRepository.findAll()).thenReturn(events);

        eventService.clearPastEvent();

        verify(eventRepository, times(1)).findAll();
        verify(executorsConfig, times(2)).executorService();
        verify(mockExecutor, times(2)).submit(any(Runnable.class));
    }

    @Test
    void create_EventCreatedAndSavedToDb_ThenReturnedAsDto() {
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventDto returned = eventService.create(eventDto);

        assertAll(
                () -> verify(eventRepository, times(1)).save(any(Event.class)),
                () -> verify(eventMapper, times(1)).toDto(event),
                () -> verify(eventMapper, times(1)).toEntity(eventDto),
                () -> assertEquals(eventDto, returned)
        );

    }

    @Test
    void getEvent_EventIsFound_ThenReturnedAsDto() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.ofNullable(event));

        EventDto returned = eventService.getEvent(event.getId());

        assertAll(
                () -> verify(eventRepository, times(1)).findById(event.getId()),
                () -> verify(eventMapper, times(1)).toDto(event),
                () -> assertEquals(eventDto, returned)
        );

    }

    @Test
    void deleteEvent_EventIsDeleted_IsValid() {
        eventService.deleteEvent(event.getId());

        verify(eventRepository, times(1)).deleteById(event.getId());
    }

    @Test
    void updateEvent_EventFoundAndUpdated_ThenSavedToDb() {
        when(eventRepository.findById(eventDto.getId())).thenReturn(Optional.ofNullable(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventDto returned = eventService.updateEvent(eventDto);

        assertAll(
                () -> verify(eventRepository, times(1)).findById(eventDto.getId()),
                () -> verify(eventMapper, times(1)).toEntity(eventDto),
                () -> verify(eventRepository, times(1)).save(any(Event.class)),
                () -> assertEquals(eventDto, returned)
        );
    }

    @Test
    void getEventsByFilter_EventFilteredByTitle_ThenReturnedAsDto() {
        List<Event> events = List.of(event);
        when(eventRepository.findAll()).thenReturn(events);
        when(eventFilter.isApplicable(eventFilterDto)).thenReturn(true);

        eventService.getEventsByFilter(eventFilterDto);

        assertAll(
                () -> verify(eventRepository, times(1)).findAll(),
                () -> verify(eventFilter, times(1)).isApplicable(eventFilterDto),
                () -> verify(eventFilter, times(1)).apply(events, eventFilterDto),
                () -> verify(eventMapper, times(1)).toDto(events)
        );

    }

    @Test
    void getOwnedEvents_OwnedEventsFound_ThenReturnedAsDto() {
        long ownerId = event.getOwner().getId();
        when(eventRepository.findAllByUserId(ownerId)).thenReturn(List.of(event));

        List<EventDto> returned = eventService.getOwnedEvents(ownerId);

        assertAll(
                () -> verify(eventRepository, times(1)).findAllByUserId(ownerId),
                () -> verify(eventMapper, times(1)).toDto(List.of(event)),
                () -> assertEquals(List.of(eventDto), returned)
        );

    }

    @Test
    void getParticipatedEvents_ParticipatedEventsFound_ThenReturnedAsDto() {
        when(eventRepository.findParticipatedEventsByUserId(4L)).thenReturn(List.of(event));
        when(eventMapper.toDto(anyList())).thenReturn(List.of(eventDto));

        List<EventDto> returned = eventService.getParticipatedEvents(4L);

        assertAll(
                () -> verify(eventRepository, times(1)).findParticipatedEventsByUserId(4L),
                () -> verify(eventMapper, times(1)).toDto(List.of(event)),
                () -> assertEquals(List.of(eventDto), returned)
        );

    }
}
