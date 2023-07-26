package school.faang.user_service.service.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.EventDateTime;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EventLinkDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final GoogleConnectionService googleConnectionService;


    public EventLinkDto pushEventToGoogle(Long eventId, Long userId) throws GeneralSecurityException, IOException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        if (!user.getParticipatedEvents().contains(event)) {
            throw new DataValidationException("User with id " + userId + " is not part of event with id " + eventId);
        }

        com.google.api.services.calendar.model.Event googleEvent = new com.google.api.services.calendar.model.Event();

        googleEvent.setSummary(event.getTitle());
        googleEvent.setDescription(event.getDescription());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        DateTime startDateTime = new DateTime(event.getStartDate().format(formatter));
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Europe/Moscow");
        googleEvent.setStart(start);

        DateTime endDateTime = new DateTime(event.getEndDate().format(formatter));
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Europe/Moscow");
        googleEvent.setEnd(end);

        String calendarId = "primary";
        Calendar service = googleConnectionService.connectAndGetCalendar();
        googleEvent = service.events().insert(calendarId, googleEvent).execute();
        System.out.printf("Event created: %s\n", googleEvent.getHtmlLink());

        return EventLinkDto.builder().link(googleEvent.getHtmlLink()).build();
    }
}
