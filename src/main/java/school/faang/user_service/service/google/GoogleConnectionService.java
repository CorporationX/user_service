package school.faang.user_service.service.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.EventDateTime;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.GoogleEventLinkDto;
import school.faang.user_service.entity.GoogleToken;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.GoogleTokenRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.util.google.JpaDataStoreFactory;

@Service
@RequiredArgsConstructor
public class GoogleConnectionService {
    private static final String APPLICATION_NAME = "Corporation-X";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final int PORT = 8081;
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final GoogleTokenRepository googleTokenRepository;

    public GoogleEventLinkDto createEvent(Long userId, Long eventId) throws GeneralSecurityException, IOException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        if (!user.getParticipatedEvents().contains(event)) {
            throw new DataValidationException("User with id " + userId + " is not part of event with id " + eventId);
        }

        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(
                HTTP_TRANSPORT,
                String.valueOf(userId)))
                .setApplicationName(APPLICATION_NAME)
                .build();

        return pushEventToGoogle(service, event);
    }

    public GoogleEventLinkDto pushEventToGoogle(Calendar service, Event event) throws IOException {

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
        googleEvent = service.events().insert(calendarId, googleEvent).execute();

        return GoogleEventLinkDto.builder().link(googleEvent.getHtmlLink()).build();
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, String userId)
            throws IOException {
        GoogleClientSecrets clientSecrets = getClientSecrets();
        GoogleAuthorizationCodeFlow flow = getGoogleAuthorizationCodeFlow(HTTP_TRANSPORT, clientSecrets);
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(PORT).build();
        // Returns an authorized Credential object.
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize(String.valueOf(userId));
    }

    private GoogleClientSecrets getClientSecrets() throws IOException {
        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Файл не найден: " + CREDENTIALS_FILE_PATH);
        }
        return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
    }

    private GoogleAuthorizationCodeFlow getGoogleAuthorizationCodeFlow(
            NetHttpTransport HTTP_TRANSPORT, GoogleClientSecrets clientSecrets) throws IOException {
        return new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new JpaDataStoreFactory(googleTokenRepository))
                .setAccessType("offline")
                .build();
    }
}
