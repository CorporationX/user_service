package school.faang.user_service.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.event.EventService;

@ExtendWith(MockitoExtension.class)
public class ClearEventsSchedulerTest {
    @Mock
    EventService eventService;

    @InjectMocks
    ClearEventsScheduler clearEventsScheduler;

    @Test
    public void clearEventsTest() {
        clearEventsScheduler.clearEvents();
        Mockito.verify(eventService, Mockito.times(1)).clearEvents();
    }
}
