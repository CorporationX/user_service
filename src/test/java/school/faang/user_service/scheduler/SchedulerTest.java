package school.faang.user_service.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SchedulerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private Scheduler scheduler;

    private final List<Event> events = new ArrayList<>();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(scheduler, "batchSize", 3);
        IntStream.range(0, 6).forEach(i -> {
            Event event = Event.builder()
                    .id(i)
                    .endDate(LocalDateTime.now().minusDays(1))
                    .build();
            events.add(event);
        });
    }

    @Test
    @DisplayName("Проверка удаления прошедших событий")
    public void testClearEvents() {
        when(eventService.getPastEvents()).thenReturn(events);
        doNothing().when(eventService).deleteEventsByIds(anyList());
        int batchSize = 3;
        int calls = (int) Math.ceil((double) events.size() / batchSize);
        scheduler.clearEvents();
        verify(eventService, times(calls)).deleteEventsByIds(anyList());

    }

    @Test
    @DisplayName("Проверка удаления пустого списка прошедших событий")
    public void testClearEventsWithEmptyEvents() {
        List<Event> emptyEvents = new ArrayList<>();
        when(eventService.getPastEvents()).thenReturn(emptyEvents);
        scheduler.clearEvents();
        verify(eventService, times(0)).deleteEventsByIds(anyList());
        verify(eventService).getPastEvents();
    }
}
