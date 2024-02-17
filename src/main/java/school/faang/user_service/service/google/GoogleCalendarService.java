package school.faang.user_service.service.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.google.GoogleCalendarConfig;
import school.faang.user_service.dto.google.EventCalendarDto;
import school.faang.user_service.mapper.google.EventCalendarMapper;
import school.faang.user_service.service.EventService;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

    private final GoogleCalendarConfig googleCalendar;
    private final EventService eventService;
    private final EventCalendarMapper mapper;

    public Event createEvent(EventCalendarDto dto)  {
        return new Event()
                .setSummary(dto.getTitle())
                .setDescription(dto.getDescription())
                .setLocation(dto.getLocation())
                .setStart(new EventDateTime().setDateTime(dto.getStartDateTime()).setTimeZone("Europe/Moscow"))
                .setEnd(new EventDateTime().setDateTime(dto.getEndDateTime()).setTimeZone("Europe/Moscow"));
    }

    public EventCalendarDto createEvent(long eventId) throws GeneralSecurityException, IOException {

//        DateTime startDateTime = new DateTime("2015-05-28T09:00:00-07:00");
//        DateTime endDateTime = new DateTime("2015-05-28T17:00:00-07:00");
//        EventDateTime start = new EventDateTime()
//                .setDateTime(startDateTime)
//                .setTimeZone("America/Los_Angeles");
//        EventDateTime end = new EventDateTime()
//                .setDateTime(endDateTime)
//                .setTimeZone("America/Los_Angeles");
//
//        Event event =  new Event()
//                .setSummary("New Event")
//                .setDescription(disc + " new disc")
//                .setStart(start)
//                .setEnd(end);
//
//
//        String calendarId = "primary";

        school.faang.user_service.entity.event.Event newEvent = eventService.getEvent(eventId);
        EventCalendarDto dto = mapper.toDto(newEvent);

        Event event1 = createEvent(dto);


        Event event = googleCalendar.getCalendar().events().insert("primary", event1).execute();
        System.out.println("Event created " + event.getHtmlLink());

        return null;
    }


}
