package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    @InjectMocks
    private EventServiceImpl eventService;

    @Mock
    private EventRepository eventRepository;

    @Test
    void deleteExpiredEvents() {
        Event event1 = new Event();
        Event event2 = new Event();
        event1.setCreatedAt(LocalDateTime.now());
        event2.setCreatedAt(LocalDateTime.now().minusWeeks(2));

        List<Event> allEvents = List.of(event1, event2);

        when(eventRepository.findAll()).thenReturn(allEvents);

        eventService.deleteExpiredEvents();

        assertEquals(allEvents.get(0), event1);
    }
}
