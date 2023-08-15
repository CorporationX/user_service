package school.faang.user_service.service.event;

import org.apache.commons.collections4.ListUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventAsyncService eventAsyncService;
    @InjectMocks
    private EventService eventService;

    private List<Event> events;

    @BeforeEach
    void setUp() {
        Event event = mock(Event.class);
        when(event.getEndDate()).thenReturn(LocalDateTime.now().minusDays(1));
        events = List.of(event, event, event);

        when(eventRepository.findAll()).thenReturn(events);
    }

    @Test
    void clearEvents_shouldSplitEventListAndInvokeClearEventsPartition() {
        eventService.clearEvents(1);

        List<List<Event>> partitions = ListUtils.partition(events, events.size());

        partitions.forEach(partition -> verify(eventAsyncService).clearEventsPartition(partition));
    }
}