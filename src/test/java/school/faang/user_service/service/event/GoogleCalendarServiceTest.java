package school.faang.user_service.service.event;

import com.google.api.services.calendar.Calendar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.mapper.event.CalendarMapperImpl;
import school.faang.user_service.repository.event.EventRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class GoogleCalendarServiceTest {
    @Mock
    private Calendar calendar;
    @Mock
    private EventRepository eventRepository;
    @Spy
    private CalendarMapperImpl calendarMapper;
    @InjectMocks
    private GoogleCalendarService googleCalendarService;

    @Test
    public void testCreateEventThrowException() {
        assertThrows(NotFoundException.class, () -> {
            googleCalendarService.createEvent(1L);
        });
    }
}