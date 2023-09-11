package school.faang.user_service.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class SchedulerTest {
    private Scheduler scheduler;
    @Mock
    private EventService eventService;

    @BeforeEach
    public void init() {
        scheduler = new Scheduler(eventService);
        ReflectionTestUtils.setField(scheduler, "batchSize", 10);
    }

    @Test
    public void clearEvents() {
        List<Event> eventsToDelete = List.of(Event.builder().build());

        Mockito.when(eventService.getEventsToDelete()).thenReturn(eventsToDelete);

        scheduler.clearEvents();
        Mockito.verify(eventService).deleteEvents(eventsToDelete);
    }
}