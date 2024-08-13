package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.thread.ThreadPoolDistributor;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.event.EventStatus.COMPLETED;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    private EventRepository eventRepository;
    @Mock
    private ThreadPoolDistributor threadPool;
    private int quantityThreadPollSize = 2;

    private EventService eventService;

    @BeforeEach
    public void setUp() {
        ThreadPoolTaskExecutor executor = Mockito.mock(ThreadPoolTaskExecutor.class);
        when(threadPool.customThreadPool()).thenReturn(executor);
        when(executor.submit(any(Runnable.class))).thenReturn(null);
        eventService = new EventService(eventRepository, threadPool, quantityThreadPollSize);
    }

    @Test
    public void testDeletingAllPastEvents() {
        var event = Event.builder().id(1L).status(COMPLETED).build();
        List<Event> completedEvents = List.of(event);
        when(eventRepository.findByStatus(COMPLETED)).thenReturn(completedEvents);

        eventService.deletingAllPastEvents();

        verify(eventRepository, times(1)).findByStatus(COMPLETED);
        verify(threadPool.customThreadPool(), times(quantityThreadPollSize)).submit(any(Runnable.class));

    }

    @Test
    public void testDeletingAllPastEventsWithNoEvents() {
        when(eventRepository.findByStatus(COMPLETED)).thenReturn(new ArrayList<>());

        eventService.deletingAllPastEvents();

        verify(eventRepository, times(1)).findByStatus(COMPLETED);
        verify(threadPool.customThreadPool(), times(quantityThreadPollSize)).submit(any(Runnable.class));
    }

    @Test
    public void testDeletingAllPastEventsWithUnevenDistribution() {
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Event event = new Event();
            event.setId((long) i);
            event.setStatus(COMPLETED);
            events.add(event);
        }
        when(eventRepository.findByStatus(COMPLETED)).thenReturn(events);

        eventService.deletingAllPastEvents();

        verify(eventRepository, times(1)).findByStatus(COMPLETED);
        verify(threadPool.customThreadPool(), times(quantityThreadPollSize)).submit(any(Runnable.class));
    }
}