package school.faang.user_service.service.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.google.GoogleCalendarConfig;
import school.faang.user_service.dto.google.EventCalendarDto;
import school.faang.user_service.mapper.google.EventCalendarMapper;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

    private final GoogleCalendarConfig googleCalendar;
    private final EventCalendarMapper mapper;

    public EventCalendarDto createEvent(long disc) throws GeneralSecurityException, IOException {

        DateTime startDateTime = new DateTime("2015-05-28T09:00:00-07:00");
        DateTime endDateTime = new DateTime("2015-05-28T17:00:00-07:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Los_Angeles");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/Los_Angeles");

        Event event = new Event()
                .setSummary("New Event")
                .setDescription(disc + " new disc")
                .setStart(start)
                .setEnd(end);


        String calendarId = "primary";

        event = googleCalendar.getCalendar().events().insert(calendarId, event).execute();
        System.out.println("Event created " + event.getHtmlLink());

        return mapper.toDto(event);
    }
}
