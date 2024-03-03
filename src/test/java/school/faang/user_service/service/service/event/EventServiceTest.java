package school.faang.user_service.service.service.event;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;

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
        verify(eventRepository).deleteAllById(List.of(1L, 2L));
    }
}
