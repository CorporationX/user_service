package school.faang.user_service.service.calendar.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.calendar.GoogleCalendarResponseDto;
import school.faang.user_service.dto.calendar.GoogleEventDto;

import java.io.IOException;
import java.security.GeneralSecurityException;
import com.google.api.services.calendar.Calendar;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {
//    private final GoogleCalendarProvider googleCalendarProvider;

    public GoogleEventDto createEvent(GoogleEventDto eventDto) throws IOException, GeneralSecurityException, GeneralSecurityException, IOException {
        Calendar googleCalendar = GoogleCalendarProvider.getGoogleCalendar();
        Calendar service = GoogleCalendarProvider.getGoogleCalendar();

        Event event = new Event()
            .setSummary(eventDto.getSummary())
            .setLocation(eventDto.getLocation())
            .setDescription(eventDto.getDescription())
            .setStart(eventDto.getStart())
            .setEnd(eventDto.getStart());


        String calendarId = "primary"; // Use "primary" for the default user's calendar
        event = googleCalendar.events().insert(calendarId, event).execute();
        System.out.printf("Event created: %s\n", event.getHtmlLink());

        return eventDto;
    }

    public GoogleCalendarResponseDto handleCallback(String code, Long userId) {
        return new GoogleCalendarResponseDto();
    }
}
