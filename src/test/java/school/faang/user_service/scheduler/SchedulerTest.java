package school.faang.user_service.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.event.EventService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SchedulerTest {
    @Mock
    private EventService eventService;

    private Scheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new Scheduler(eventService);
    }

    @Test
    void testClearEvents() {
        scheduler.clearEvents();
        verify(eventService, times(1)).deletePastEvents(any(Integer.class));
    }

}