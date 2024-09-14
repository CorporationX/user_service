package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventValidator validator;

    @InjectMocks
    private EventServiceImpl eventService;

    private List<Event> events;

    @BeforeEach
    void setup(){
        events = List.of(new Event());
    }

    @Test
    void testRemoveEvents(){
        eventService.removeEvents(events);

        verify(validator).validateEvents(events);
        verify(eventRepository).deleteAll(events);
    }
}
