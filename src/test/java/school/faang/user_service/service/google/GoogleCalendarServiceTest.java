package school.faang.user_service.service.google;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.EventDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.calendar.GoogleEventDto;
import com.google.api.services.calendar.model.Event;
import school.faang.user_service.dto.calendar.GoogleEventResponseDto;
import school.faang.user_service.mapper.GoogleCalendarMapper;
import school.faang.user_service.service.google.calendar.GoogleCalendarProvider;
import school.faang.user_service.service.google.calendar.GoogleCalendarService;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class GoogleCalendarServiceTest {
    @Mock
    private GoogleCalendarMapper googleCalendarMapper;

    @Mock
    private GoogleCalendarProvider googleCalendarProvider;

    @Mock
    private Calendar mockCalendarService;

    @InjectMocks
    private GoogleCalendarService googleCalendarService;

    private String calendarId = "primary";

    @Test
    public void createEventTest() throws GeneralSecurityException, IOException {
        GoogleEventDto googleEventDto = new GoogleEventDto();
        googleEventDto.setDescription("test");
        googleEventDto.setSummary("test");
        googleEventDto.setLocation("test");
        googleEventDto.setStartDate(new EventDateTime());
        googleEventDto.setEndDate(new EventDateTime());

        Event event = new Event();
        event.setDescription("test");
        event.setSummary("test");
        event.setStart(new EventDateTime());
        event.setEnd(new EventDateTime());
        event.setLocation("test");
        event.setHtmlLink("test-link");

        Mockito.when(googleCalendarMapper.toGoogleEvent(googleEventDto)).thenReturn(event);
        Mockito.when(googleCalendarProvider.getCalendar()).thenReturn(mockCalendarService);

        Calendar.Events events = mock(Calendar.Events.class);

        Mockito.when(mockCalendarService.events()).thenReturn(events);

        Calendar.Events.Insert insertRequest = mock(Calendar.Events.Insert.class);

        insertRequest.set(calendarId, event);

        Mockito.when(events.insert(calendarId, event)).thenReturn(insertRequest);
        Mockito.when(insertRequest.execute()).thenReturn(event);

        googleCalendarService.setCalendarId(calendarId);

        GoogleEventResponseDto createdEvent = googleCalendarService.createEvent(googleEventDto);

        Mockito.verify(events).insert(calendarId, event);
        Mockito.verify(insertRequest).execute();

        Mockito.verify(googleCalendarProvider, Mockito.times(1)).getCalendar();
        Mockito.verify(googleCalendarMapper, Mockito.times(1)).toGoogleEvent(googleEventDto);

        assertEquals(createdEvent.getMessage(), GoogleCalendarService.SUCCESSFUL_MESSAGE);
        assertEquals(createdEvent.getLink(), "test-link");
    }
}
