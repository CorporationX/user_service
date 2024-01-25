package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.event.EventRepository;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;


    @Test
    void shouldGetEventService() {
        long eventId = 1;
        eventService.getEvent(eventId);
        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    void shouldDeleteEventService() {
        long eventId = 1;
        eventService.deleteEvent(eventId);
        verify(eventRepository, times(1)).deleteById(eventId);
    }

}



