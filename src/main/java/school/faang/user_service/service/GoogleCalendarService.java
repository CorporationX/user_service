package school.faang.user_service.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.EventDateTime;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.GoogleToken;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.GoogleTokenRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class GoogleCalendarService {
    private final EventRepository eventRepository;
    private final GoogleTokenRepository googleTokenRepository;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String APP_NAME = "corporation-x";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CREDENTIALS_FILE_PATH = "/google-calendar-credentials.json";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    @Transactional
    public String createCalendarEvent(Long eventId) throws IOException, GeneralSecurityException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException(
                        MessageFormat.format("Event {0} not found", eventId)));

        GoogleToken googleToken = googleTokenRepository.findByUser(event.getOwner());

        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleClientSecrets googleClientSecrets = getClientSecrets();
        GoogleAuthorizationCodeFlow flow = getFlow(httpTransport, googleClientSecrets);

        if (googleToken != null && googleToken.getAccessToken() != null){
            return sendEventToCalendar(event, httpTransport, googleClientSecrets, googleToken).getHtmlLink();
        }

        return getRedirectionUrl(event.getId(), flow).build();
   }

    @Transactional
    public void getCredentialsFromCallback(String code, Long eventId) throws IOException, GeneralSecurityException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException(
                MessageFormat.format("Event {0} not found", eventId)));

        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleClientSecrets googleClientSecrets = getClientSecrets();
        GoogleAuthorizationCodeFlow flow = getFlow(httpTransport, googleClientSecrets);

        GoogleTokenResponse response = flow.newTokenRequest(code)
                .setRedirectUri("http://localhost:8080/api/v1/calendar/auth/callback?event=" + event.getId())
                .execute();

        Credential credential = flow.createAndStoreCredential(response, String.valueOf(event.getOwner().getId()));
        GoogleToken googleToken = buildGoogleToken(credential, event.getOwner(), googleClientSecrets);
        sendEventToCalendar(event, httpTransport, googleClientSecrets, googleToken);
    }

    private com.google.api.services.calendar.model.Event sendEventToCalendar(
            Event event, NetHttpTransport httpTransport, GoogleClientSecrets googleClientSecrets,
            GoogleToken googleToken) throws IOException {
        Credential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(googleClientSecrets)
                .build();

        credential.setAccessToken(googleToken.getAccessToken());
        credential.setRefreshToken(googleToken.getRefreshToken());
        credential.setExpirationTimeMilliseconds(googleToken.getExpirationTimeMilliseconds());

        Calendar calendar =
                new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                        .setApplicationName(APP_NAME)
                        .build();

        com.google.api.services.calendar.model.Event googleCalendarEvent = createGoogleCalendarEvent(event);
        String calendarId = "primary";
        googleCalendarEvent = calendar.events().insert(calendarId, googleCalendarEvent).execute();

        return googleCalendarEvent;
    }

    private com.google.api.services.calendar.model.Event createGoogleCalendarEvent(Event event){
        com.google.api.services.calendar.model.Event googleCalendarEvent = new com.google.api.services.calendar.model.Event()
                .setSummary(event.getTitle())
                .setLocation(event.getLocation())
                .setDescription(event.getDescription());

        ZoneOffset zoneOffset = ZoneOffset.UTC;

        ZonedDateTime zonedStart = ZonedDateTime.of(event.getStartDate(), zoneOffset);
        ZonedDateTime zonedEnd = ZonedDateTime.of(event.getEndDate(), zoneOffset);

        DateTimeFormatter rfc3339Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

        String formattedStart = rfc3339Formatter.format(zonedStart);
        String formattedEnd = rfc3339Formatter.format(zonedEnd);

        DateTime startDateTime = new DateTime(formattedStart);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("UTC");
        googleCalendarEvent.setStart(start);

        DateTime endDateTime = new DateTime(formattedEnd);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("UTC");
        googleCalendarEvent.setEnd(end);

        return googleCalendarEvent;
    }

    private GoogleAuthorizationCodeRequestUrl getRedirectionUrl(Long eventId, GoogleAuthorizationCodeFlow flow) {
        return flow.newAuthorizationUrl()
                .setAccessType("offline")
                .setRedirectUri("http://localhost:8080/api/v1/calendar/auth/callback?event=" + eventId);
    }

    private GoogleToken buildGoogleToken(Credential credential, User user, GoogleClientSecrets googleClientSecrets){
        return GoogleToken.builder()
                .uuid(UUID.randomUUID().toString())
                .user(user)
                .accessToken(credential.getAccessToken())
                .refreshToken(credential.getAccessToken())
                .expirationTimeMilliseconds(credential.getExpirationTimeMilliseconds())
                .updatedAt(LocalDateTime.now())
                .oauthClientSecret(googleClientSecrets.getDetails().getClientSecret())
                .oauthClientId(googleClientSecrets.getDetails().getClientId())
                .build();
    }

    private GoogleClientSecrets getClientSecrets() throws IOException {
        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("File not found: " + CREDENTIALS_FILE_PATH);
        }

        return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
    }

    private GoogleAuthorizationCodeFlow getFlow(NetHttpTransport httpTransport, GoogleClientSecrets clientSecrets)
            throws GeneralSecurityException, IOException {
        return new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
    }
}
