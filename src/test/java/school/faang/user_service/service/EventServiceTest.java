package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.event.EventStatus.COMPLETED;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ExecutorService threadPool;

    private int quantityThreadPollSize;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(eventService, "quantityThreadPollSize", 10);
    }

    @Test
    public void testDeletingAllPastEvents() {
        var event = Event.builder().id(1L).status(COMPLETED).build();
        List<Event> completedEvents = List.of(event);
        when(eventRepository.findByStatus(COMPLETED)).thenReturn(completedEvents);

        eventService.deletingAllPastEvents();

        verify(eventRepository, times(1)).findByStatus(COMPLETED);
        verify(threadPool, times(10)).submit(any(Runnable.class));

    }

    @Test
    public void testDeletingAllPastEventsWithNoEvents() {
        when(eventRepository.findByStatus(COMPLETED)).thenReturn(new ArrayList<>());

        eventService.deletingAllPastEvents();

        verify(eventRepository, times(1)).findByStatus(COMPLETED);
        verify(threadPool, times(10)).submit(any(Runnable.class));
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
        verify(threadPool, times(10)).submit(any(Runnable.class));
    }
}