package school.faang.user_service.service.service.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private ThreadPoolExecutor threadPoolExecutor;

    @Test
    public void testEventsAreEmpty() {
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> eventService.clearEvents());
        assertEquals("There are no completed events in DB", illegalArgumentException.getMessage());
    }

    @Test
    public void testCompletedEventsNotFound() {
        when(eventRepository.findAll().stream()
                .filter(event -> event.getStatus().equals(EventStatus.COMPLETED) || event.getStatus().equals(EventStatus.CANCELED))
                .map(Event::getId).toList()).thenReturn(Collections.emptyList());

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> eventService.clearEvents());
        assertEquals("There are no completed events in DB", illegalArgumentException.getMessage());
    }

    @Test
    public void testClearEventsSuccessful() {
        eventService.setBatchSize(1000);
        Event firstEvent = new Event();
        firstEvent.setId(1L);
        firstEvent.setStatus(EventStatus.COMPLETED);

        Event secondEvent = new Event();
        secondEvent.setId(2L);
        secondEvent.setStatus(EventStatus.COMPLETED);

        Event thirdEvent = new Event();
        thirdEvent.setId(3L);
        thirdEvent.setStatus(EventStatus.IN_PROGRESS);

        List<Event> events = List.of(firstEvent, secondEvent, thirdEvent);
        when(eventRepository.findAll()).thenReturn(events);

        eventService.clearEvents();
        verify(eventRepository, times(1)).findAll();
        verify(threadPoolExecutor, times(1)).execute(any(Runnable.class));
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(threadPoolExecutor).execute(runnableCaptor.capture());
        Runnable capturedRunnable = runnableCaptor.getValue();
        capturedRunnable.run();
        verify(eventRepository).deleteAllById(List.of(1L, 2L));
    }


}
