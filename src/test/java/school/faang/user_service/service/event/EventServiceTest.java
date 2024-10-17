package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private EventStartEventPublisher eventStartEventPublisher;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event event;
    private EventDto eventDto;
    private LocalDateTime startTime = LocalDateTime.of(2220, 1, 1, 0, 0);

    @BeforeEach
    void setUp() {
        eventDto = new EventDto(List.of(1L, 2L), 100L, "Test Event", LocalDateTime.now().minusDays(1));
        event = new Event();
        event.setId(100L);
        event.setTitle("Test Event");
        event.setStatus(EventStatus.PLANNED);
    }

    @Test
    @DisplayName("Получение прошедших событий")
    public void testGetPastEvents() {
        when(eventRepository.findAllByEndDateBefore(any(LocalDateTime.class))).thenReturn(List.of());

        List<Event> result = eventService.getPastEvents();

        assertTrue(result.isEmpty());

        verify(eventRepository).findAllByEndDateBefore(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Удаление событий по идентификаторам")
    public void testDeleteEventsByIds() {
        doNothing().when(eventRepository).deleteAllById(List.of(1L, 2L, 3L));

        eventService.deleteEventsByIds(List.of(1L, 2L, 3L));

        verify(eventRepository).deleteAllById(List.of(1L, 2L, 3L));
    }

    @Test
    public void testGetEvent_Success() {
        List<Long> ids = List.of(1L);
        Event event = new Event();
        event.setTitle("test");
        event.setStartDate(startTime);
        event.setId(1L);
        EventDto eventDto = new EventDto(ids, 1L, "test", startTime);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toEventDto(event)).thenReturn(eventDto);

        EventDto result = eventService.getEvent(1L);

        assertEquals(eventDto.eventId(), result.eventId());
        assertEquals(eventDto.title(), result.title());
        assertEquals(eventDto.startDateTime(), result.startDateTime());
    }

    @Test
    void testStartEvent_EventIsInProgress() {
        event.setStatus(EventStatus.IN_PROGRESS);
        when(eventRepository.findById(eventDto.eventId())).thenReturn(Optional.of(event));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.startEvent(event.getId());
        });

        assertEquals("Event with id 100 can`t be started because it is IN_PROGRESS", exception.getMessage());
    }

    @Test
    void testStartEvent_WhenEventNotStartedYet() {
        event.setStartDate(startTime.minusYears(1000));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.startEvent(event.getId());
        });

        assertEquals("Event with id 100 is not started", exception.getMessage());
    }

    @Test
    void testStartEvent_Success() {
        event.setStartDate(startTime.plusDays(1));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        eventService.startEvent(event.getId());

        assertEquals(EventStatus.IN_PROGRESS, event.getStatus());
        verify(eventRepository, times(1)).save(event);
        verify(eventStartEventPublisher, times(1)).publish(any(EventDto.class));
    }
}
