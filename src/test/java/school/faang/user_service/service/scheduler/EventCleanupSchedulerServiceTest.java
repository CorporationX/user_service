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

@ExtendWith(MockitoExtension.class)
class EventCleanupSchedulerServiceTest {
    @Mock
    EventService eventService;

    @InjectMocks
    EventCleanupSchedulerService eventSchedulerService;

    @Test
    public void testClearPastEvents() {
        Event event = Event.builder().endDate(LocalDateTime.now()).build();
        eventSchedulerService.setBatchSize(50);
        Mockito.when(eventService.getAllEvents()).thenReturn(List.of(event));
        eventSchedulerService.clearPastEvents();

        Mockito.verify(eventService).getAllEvents();
        Mockito.verify(eventService).deleteByIds(Mockito.anySet());
    }
}