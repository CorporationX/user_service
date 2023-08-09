package school.faang.user_service.service.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.google.GoogleCalendarConfig;
import school.faang.user_service.dto.GoogleEventResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.GoogleTokenRepository;
import school.faang.user_service.service.EventService;
import school.faang.user_service.service.UserService;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    private final GoogleTokenRepository googleTokenRepository;
    private final EventService eventService;
    private final UserService userService;
    private final GoogleCalendarConfig calendarConfig;
    private final GoogleAuthorizationCodeFlow authorizationCodeFlow;
    private final NetHttpTransport httpTransport;

    @Transactional
    public GoogleEventResponseDto createEvent(Long userId, Long eventId) throws IOException {
        Event event = eventService.getEvent(eventId);
        User user = userService.getUser(userId);

        if (!user.getParticipatedEvents().contains(event)) {
            throw new DataValidationException("User with id " + userId + " is not part of event with id " + eventId);
        }

        if (!googleTokenRepository.existsByUser(user)) {
            return GoogleEventResponseDto.builder()
                    .message("Follow the link to authorize your calendar")
                    .link(getAuthorizationLink(userId, eventId))
                    .build();
        }

        Credential credential = authorizationCodeFlow.loadCredential(String.valueOf(userId));
        Calendar service = getService(credential);

        com.google.api.services.calendar.model.Event googleEvent = mapToGoogleEvent(event, service);
        validateGoogleEvent(googleEvent, service);
        googleEvent = service.events().insert(calendarConfig.getCalendarId(), googleEvent).execute();

        return getResponse(googleEvent.getHtmlLink());
    }

    @Transactional
    public GoogleEventResponseDto handleCallback(String code, Long userId, Long eventId) throws IOException {
        TokenResponse response = authorizationCodeFlow.newTokenRequest(code)
                .setRedirectUri(calendarConfig.getRedirectUri())
                .execute();
        Credential credential = authorizationCodeFlow.createAndStoreCredential(response, String.valueOf(userId));
        Calendar service = getService(credential);

        Event event = eventService.getEvent(eventId);
        com.google.api.services.calendar.model.Event googleEvent = mapToGoogleEvent(event, service);
        validateGoogleEvent(googleEvent, service);
        googleEvent = service.events().insert(calendarConfig.getCalendarId(), googleEvent).execute();

        return getResponse(googleEvent.getHtmlLink());
    }

    private com.google.api.services.calendar.model.Event mapToGoogleEvent(
            Event event, Calendar service) throws IOException {

        com.google.api.services.calendar.model.Event googleEvent = new com.google.api.services.calendar.model.Event()
                .setSummary(event.getTitle())
                .setDescription(event.getDescription())
                .setLocation(event.getLocation());

        String userTimeZone = service.calendars().get(calendarConfig.getCalendarId()).execute().getTimeZone();

        ZonedDateTime zonedStart = ZonedDateTime.of(event.getStartDate(), ZoneId.of(userTimeZone));
        ZonedDateTime zonedEnd = ZonedDateTime.of(event.getEndDate(), ZoneId.of(userTimeZone));

        googleEvent.setStart(getEventDateTime(zonedStart));
        googleEvent.setEnd(getEventDateTime(zonedEnd));

        return googleEvent;
    }

    private void validateGoogleEvent(com.google.api.services.calendar.model.Event googleEvent, Calendar service)
            throws IOException {
        Events events = service.events().list(calendarConfig.getCalendarId()).execute();

        boolean eventAlreadyExists = events.getItems()
                .stream()
                .anyMatch(event -> event.getSummary().equals(googleEvent.getSummary())
                        && event.getDescription().equals(googleEvent.getDescription())
                        && event.getLocation().equals(googleEvent.getLocation())
                        && event.getStart().getDateTime().equals(googleEvent.getStart().getDateTime())
                        && event.getEnd().getDateTime().equals(googleEvent.getEnd().getDateTime()));

        if (eventAlreadyExists) {
            throw new DataValidationException("This event already exists in your calendar");
        }
    }

    private EventDateTime getEventDateTime(ZonedDateTime zonedDateTime) {
        DateTime dateTime = new DateTime(zonedDateTime.format(FORMATTER));
        return new EventDateTime().setDateTime(dateTime);
    }

    private String getAuthorizationLink(Long userId, Long eventId) {
        return authorizationCodeFlow.newAuthorizationUrl()
                .setRedirectUri(calendarConfig.getRedirectUri())
                .setState(userId + "-" + eventId)
                .build();
    }

    private Calendar getService(Credential credential) {
        return new Calendar.Builder(httpTransport, calendarConfig.getJsonFactory(), credential)
                .setApplicationName(calendarConfig.getApplicationName())
                .build();
    }

    private GoogleEventResponseDto getResponse(String link) {
        return GoogleEventResponseDto.builder()
                .message("The event has been successfully added to your Google calendar.")
                .link(link)
                .build();
    }
}
