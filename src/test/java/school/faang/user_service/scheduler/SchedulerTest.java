package school.faang.user_service.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.impl.event.EventServiceImpl;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SchedulerTest {

    @Mock
    private EventServiceImpl eventServiceImpl;

    @InjectMocks
    private Scheduler scheduler;

    @Test
    public void testClearEvents() {
        // given & when
        scheduler.clearEvents();
        // then
        verify(eventServiceImpl).deletePassedEvents();
    }
}
