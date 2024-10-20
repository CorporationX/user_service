package school.faang.user_service.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "scheduler.thread-count=10")
class SchedulerTest {

    @Mock
    EventRepository eventRepository;

    @Mock
    EventService eventService;

    @InjectMocks
    EventClearScheduler eventClearScheduler;

    Event nonExpiredEvent;
    Event expiredEvent;
    List<Event> eventList;

    @Value("${scheduler.thread-count}")
    private int threadCounts;

    @BeforeEach
    void setUp() {
        expiredEvent = new Event();
        expiredEvent.setId(1);
        expiredEvent.setEndDate(LocalDateTime.now().minusDays(1));

        nonExpiredEvent = new Event();
        nonExpiredEvent.setId(2);
        nonExpiredEvent.setEndDate(LocalDateTime.now().plusDays(2));

        //6 действующих 11 законченных
        eventList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            eventList.add(nonExpiredEvent);
        }
        for (int i = 0; i < 11; i++) {
            eventList.add(expiredEvent);
        }
    }

    @Test
    void testClearEvents() {
        when(eventRepository.getOutdatedEvents()).thenReturn(eventList);

        eventClearScheduler.setThreadCounts(threadCounts);

        eventClearScheduler.clearEvents();

        verify(eventService, times(2)).clearOutdatedEvents(any());
    }
}