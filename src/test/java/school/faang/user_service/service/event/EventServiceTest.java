package school.faang.user_service.service.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
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
    private UserService userService;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private EventDtoValidator validator;
    @Mock
    private EventTitleFilter titleFilter;

    private final Long testId = 1L;

    @Test
    public void testCreate() {
        EventDto inputDto = getInputDto();
        EventDto outputDto = getOutputDto();
        Event event = getEvent();
        User user = getUser();
        Event savedEvent = getSavedEvent();

        when(eventMapper.toEntity(inputDto)).thenReturn(event);
        when(userService.findById(inputDto.getOwnerId())).thenReturn(user);
        when(eventRepository.save(event)).thenReturn(savedEvent);
        when(eventMapper.toDto(savedEvent)).thenReturn(outputDto);

        EventDto result = eventService.create(inputDto);
        Assertions.assertSame(result, outputDto);
    }

    @Test
    public void testInvalidEventIdGet() {
        when(eventRepository.findById(testId)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                DataValidationException.class,
                () -> eventService.getEvent(testId)
        );
    }

    @Test
    public void testValidEventIdGet() {
        Event event = getEvent();
        EventDto outputDto = getOutputDto();

        when(eventRepository.findById(testId)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(outputDto);

        EventDto result = eventService.getEvent(testId);
        Assertions.assertSame(result, outputDto);
    }

    @Test
    public void testGetEventsByFilters() {
        List<Event> events = getEvents();
        List<EventDto> eventDtos = getEventDtoList();

        List<EventFilter> eventFilters = List.of(titleFilter);
        EventService service = new EventService(eventRepository, userService, eventMapper, validator, eventFilters);
        EventFilterDto filters = Mockito.mock(EventFilterDto.class);

        when(eventRepository.findAll()).thenReturn(events);
        when(eventMapper.toListDto(events)).thenReturn(eventDtos);

        List<EventDto> eventsByFilters = service.getEventsByFilters(filters);
        Assertions.assertSame(eventsByFilters, eventDtos);
    }

    @Test
    public void testInvalidEventIdDelete() {
        when(eventRepository.existsById(testId)).thenReturn(false);
        Assertions.assertThrows(
                DataValidationException.class,
                () -> eventService.deleteEvent(testId)
        );
    }

    @Test
    public void testValidEventIdDelete() {
        when(eventRepository.existsById(testId)).thenReturn(true);
        eventService.deleteEvent(testId);
        verify(eventRepository, times(1)).deleteById(testId);
    }

    @Test
    public void testInvalidEventDtoUpdate() {
        EventDto inputDto = getInputDto();

        when(eventRepository.findById(inputDto.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(
                DataValidationException.class,
                () -> eventService.updateEvent(inputDto)
        );
    }

    @Test
    public void testValidEventDtoUpdate() {
        EventDto inputDto = getInputDto();
        Event event = getEvent();
        EventDto outputDto = getOutputDto();

        when(eventRepository.findById(inputDto.getId())).thenReturn(Optional.of(event));
        when(eventMapper.toEntity(inputDto)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(outputDto);
        EventDto result = eventService.updateEvent(inputDto);

        Assertions.assertSame(result, outputDto);
    }

    @Test
    public void testGetOwnedEvents() {
        List<Event> events = getEvents();
        List<EventDto> eventDtos = getEventDtoList();

        when(eventRepository.findAllByUserId(testId)).thenReturn(events);
        when(eventMapper.toListDto(events)).thenReturn(eventDtos);
        List<EventDto> result = eventService.getOwnedEvents(testId);

        Assertions.assertSame(result, eventDtos);
    }

    @Test
    public void testParticipatedEvents() {
        List<Event> events = getEvents();
        List<EventDto> eventDtos = getEventDtoList();

        when(eventRepository.findParticipatedEventsByUserId(testId)).thenReturn(events);
        when(eventMapper.toListDto(events)).thenReturn(eventDtos);
        List<EventDto> result = eventService.getParticipatedEvents(testId);

        Assertions.assertSame(result, eventDtos);
    }

    @Test
    public void testClearExpiredEventsWhenNoEvents() {
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());
        CompletableFuture<Void> events = eventService.clearExpiredEvents();
        assertThat(events).isCompleted();
        verify(eventRepository, times(0)).deleteAllById(anyList());
    }

    @Test
    public void testClearExpiredEventsWhenNoExpiredOnes() {
        List<Event> events = List.of(
                createActiveEvent(1L, "Event1", 10L),
                createActiveEvent(2L, "Event2", 2L)
        );
        when(eventRepository.findAll()).thenReturn(events);
        CompletableFuture<Void> result = eventService.clearExpiredEvents();
        assertThat(result).isCompleted();

        verify(eventRepository, times(1)).findAll();
        verify(eventRepository, times(0)).deleteAllById(anyList());
    }

    private EventDto getInputDto() {
        return EventDto.builder()
                .id(testId)
                .build();
    }

    private EventDto getOutputDto() {
        return EventDto.builder()
                .id(testId + 1)
                .build();
    }

    private Event createExpiredEvent(long id, String title, long minusDays) {
        return Event.builder()
                .id(id)
                .endDate(LocalDateTime.now().minusDays(minusDays))
                .title(title).build();
    }

    private Event createActiveEvent(long id, String title, long plusDays) {
        return Event.builder()
                .id(id)
                .endDate(LocalDateTime.now().plusDays(plusDays))
                .title(title).build();
    }

    private List<EventDto> getEventDtoList() {
        return List.of(getOutputDto());
    }

    private Event getEvent() {
        return Event.builder().build();
    }

    private List<Event> getEvents() {
        return List.of(getEvent());
    }

    private Event getSavedEvent() {
        return Event.builder()
                .id(testId)
                .build();
    }

    private User getUser() {
        return User.builder().build();
    }
}