package school.faang.user_service.service.google.calendar;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.calendar.GoogleEventResponseDto;
import school.faang.user_service.dto.calendar.GoogleEventDto;
import school.faang.user_service.mapper.GoogleCalendarMapper;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarService {
    private final GoogleCalendarMapper googleCalendarMapper;

    @Lazy(value = true)
    private final GoogleCalendarProvider googleCalendarProvider;

    public static final String SUCCESSFUL_MESSAGE = "Event was successfully created";

    @Setter
    @Value("${google.calendar.calendar-id}")
    public String calendarId;

    public GoogleEventResponseDto createEvent(GoogleEventDto eventDto) throws GeneralSecurityException, IOException {
        Calendar googleCalendar = googleCalendarProvider.getCalendar();

        Event event = googleCalendarMapper.toGoogleEvent(eventDto);

        Calendar.Events events = googleCalendar.events();
        Calendar.Events.Insert insert = events.insert(calendarId, event);
        event = insert.execute();

        log.info("Event created: %s\n" + event.getHtmlLink());

        return new GoogleEventResponseDto(
            SUCCESSFUL_MESSAGE,
            event.getHtmlLink()
        );
    }
}
