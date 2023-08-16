package school.faang.user_service.service.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.EventCleanupSchedulerService;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
class EventCleanupSchedulerServiceTest {
    ThreadPoolExecutor threadPoolExecutor;

    @Mock
    EventService eventService;

    @InjectMocks
    EventCleanupSchedulerService eventSchedulerService;

    @Test
    public void testClearPastEvents() {
        threadPoolExecutor = new ThreadPoolExecutor(1, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        eventSchedulerService = new EventCleanupSchedulerService(eventService, threadPoolExecutor);
        Event event = Event.builder().endDate(LocalDateTime.now()).build();
        eventSchedulerService.setBatchSize(50);
        Mockito.when(eventService.getAllEvents()).thenReturn(List.of(event));
        eventSchedulerService.clearPastEvents();

        Mockito.verify(eventService).getAllEvents();
        Mockito.verify(eventService).deleteByIds(Mockito.anySet());
    }
}