package school.faang.user_service.service.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class GoogleEventService {
    private final GoogleCalendarService googleCalendarService;

    public void createEvent(String userEmailAddress, String summary, String description, String location, Date startDate, Date endDate) throws IOException, GeneralSecurityException, GeneralSecurityException, IOException {
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
//        event = service.events().insert(calendarId, event).execute();
//        System.out.printf("Event created: %s\n", event.getHtmlLink());
    }
}
