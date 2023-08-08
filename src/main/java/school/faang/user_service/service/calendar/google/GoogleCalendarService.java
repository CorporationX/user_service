package school.faang.user_service.service.calendar.google;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.calendar.GoogleCalendarResponseDto;
import school.faang.user_service.dto.calendar.GoogleEventDto;

import java.io.IOException;
import java.security.GeneralSecurityException;
//import com.google.api.services.calendar.Calendar;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

    public GoogleEventDto createEvent(GoogleEventDto eventDto) throws IOException, GeneralSecurityException, GeneralSecurityException, IOException {
       // Calendar googleCalendar = GoogleCalendarProvider.getGoogleCalendar();
//        Calendar service = googleCalendarService.getCalendarService(userEmailAddress);
//
//        Event event = new Event()
//            .setSummary(summary)
//            .setLocation(location)
//            .setDescription(description)
//            .setStart(new EventDateTime().setDateTime(new DateTime(startDate)))
//            .setEnd(new EventDateTime().setDateTime(new DateTime(endDate)));
//
//
//        String calendarId = "primary"; // Use "primary" for the default user's calendar
//        event = googleCalendar.events().insert(calendarId, event).execute();
//        System.out.printf("Event created: %s\n", event.getHtmlLink());

        GoogleEventDto tempResult = new GoogleEventDto();
        return tempResult;
    }

    public GoogleCalendarResponseDto handleCallback(String code, Long userId) {
        return new GoogleCalendarResponseDto();
    }
}
