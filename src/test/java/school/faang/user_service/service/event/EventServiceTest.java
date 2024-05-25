package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.filter.event.EventOwnerIdFilter;
import school.faang.user_service.filter.event.EventTitlePatternFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.EventMapperImpl;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.publisher.EventStartEventPublisher;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    private EventValidator eventValidator;
    @Spy
    private EventMapper eventMapper;
    @Mock
    ExecutorService executorService;
    private final List<EventFilter> eventFilters = new ArrayList<>();
    @Mock
    private EventStartEventPublisher startEventPublisher;

    @Captor
    private ArgumentCaptor<Event> eventArgumentCaptor;

    @BeforeEach
    void setUp() {
        eventFilters.add(new EventTitlePatternFilter());
        eventFilters.add(new EventOwnerIdFilter());
        eventService = new EventService(eventRepository, eventValidator, eventMapper, eventFilters, startEventPublisher, executorService);
    }

    @Test
    public void testCreate() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("title");
        eventService.createEvent(eventDto);
        verify(eventRepository, times(1)).save(eventArgumentCaptor.capture());
        Event capturedEvent = eventArgumentCaptor.getValue();
        assertEquals(eventDto.getTitle(), capturedEvent.getTitle());
    }

    @Test
    public void testGetByInvalidId() {
        long eventId = 1L;
        Optional<Event> optionalEvent = Optional.empty();
        when(eventRepository.findById(eventId)).thenReturn(optionalEvent);
        assertThrows(DataValidationException.class, () -> eventService.getEvent(eventId));
    }

    @Test
    public void testGetByValidId() {
        long eventId = 1L;
        Optional<Event> optionalEvent = Optional.of(new Event());
        when(eventRepository.findById(eventId)).thenReturn(optionalEvent);
        assertDoesNotThrow(() -> eventService.getEvent(eventId));
    }

    @Test
    public void testGetByTitlePatternFilter() {
        EventFilterDto eventFilterDto = new EventFilterDto();
        String pattern = "pattern";
        eventFilterDto.setTitlePattern(pattern);
        Event validEvent = new Event();
        validEvent.setTitle("contains " + pattern + " string");
        Event invalidEvent = new Event();
        invalidEvent.setTitle("does not contain");
        when(eventRepository.findAll()).thenReturn(List.of(validEvent, invalidEvent));
        List<EventDto> resultList = eventService.getEventsByFilter(eventFilterDto);
        assertEquals(resultList.get(0).getTitle(), validEvent.getTitle());
        assertEquals(resultList.size(), 1);
    }

    @Test
    public void testGetByOwnerIdFilter() {
        EventFilterDto eventFilterDto = new EventFilterDto();
        long ownerId = 1L;
        eventFilterDto.setOwnerId(ownerId);
        Event validEvent = new Event();
        validEvent.setOwner(User.builder().id(ownerId).build());
        Event invalidEvent = new Event();
        invalidEvent.setOwner(User.builder().id(ownerId+1L).build());
        when(eventRepository.findAll()).thenReturn(List.of(validEvent, invalidEvent));
        List<EventDto> resultList = eventService.getEventsByFilter(eventFilterDto);
        assertEquals(resultList.get(0).getOwnerId(), validEvent.getOwner().getId());
        assertEquals(resultList.size(), 1);
    }

    @Test
    public void testDelete() {
        long eventId = 1L;
        eventService.deleteEvent(eventId);
        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    public void testUpdate() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("title");
        eventService.updateEvent(eventDto);
        verify(eventRepository, times(1)).save(eventArgumentCaptor.capture());
        Event capturedEvent = eventArgumentCaptor.getValue();
        assertEquals(eventDto.getTitle(), capturedEvent.getTitle());
    }

    @Test
    public void testGetOwnedEvents() {
        long ownerId = 1L;

        eventService.getOwnedEvents(ownerId);
        verify(eventRepository, times(1)).findAllByUserId(ownerId);
    }

    @Test
    public void testGetParticipatedEvents() {
        long ownerId = 1L;
        eventService.getParticipatedEvents(ownerId);
        verify(eventRepository, times(1)).findParticipatedEventsByUserId(ownerId);
    }

    @Test
    public void testClearEvents() {
        List<Event> eventList = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            Event  event = new Event();
            long day = i % 2 == 0 ? 1 : -1;
            event.setEndDate(LocalDateTime.now().minusDays(day));
            eventList.add(event);
        }
        when(eventRepository.findAll()).thenReturn(eventList);
        ReflectionTestUtils.setField(eventService, "batchSize", 100);
        eventService.clearEvents();
        verify(eventRepository, times(1)).findAll();
        verify(executorService).execute(any(Runnable.class));
    }
}