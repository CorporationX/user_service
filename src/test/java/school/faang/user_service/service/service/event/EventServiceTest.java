package school.faang.user_service.service.service.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.util.ArrayList;
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
    public void testEventServiceEmptyList() {
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> eventService.clearEvents());
        assertEquals("There are no completed events in DB", illegalArgumentException.getMessage());
    }

    @Test
    public void testEventServiceCompletedEventsNotFound() {
        when(eventRepository.findAll().stream()
                .filter(event -> event.getStatus().equals(EventStatus.COMPLETED) || event.getStatus().equals(EventStatus.CANCELED))
                .map(Event::getId).toList()).thenReturn(Collections.emptyList());

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> eventService.clearEvents());
        assertEquals("There are no completed events in DB", illegalArgumentException.getMessage());
    }

    @Test
    public void testClearEventsSuccessful() {
        Event firstEvent = new Event();
        firstEvent.setId(1L);
        firstEvent.setStatus(EventStatus.COMPLETED);

        Event secondEvent = new Event();
        secondEvent.setId(2L);
        secondEvent.setStatus(EventStatus.COMPLETED);

        Event thirdEvent = new Event();
        thirdEvent.setId(3L);
        thirdEvent.setStatus(EventStatus.IN_PROGRESS);

        List<Event> events = new ArrayList<>();
        events.add(firstEvent);
        events.add(secondEvent);
        events.add(thirdEvent);
        when(eventRepository.findAll()).thenReturn(events);

        eventService.clearEvents();
        verify(eventRepository, times(1)).deleteAllById(List.of(1L, 2L));
    }
}
