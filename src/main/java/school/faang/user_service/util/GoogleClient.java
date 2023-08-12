package school.faang.user_service.util;

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
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.GoogleToken;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.GoogleTokenRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GoogleClient {
    private static final String APPLICATION_NAME = "CorporationX";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/json/googleSecret.json";

    public GoogleAuthorizationCodeFlow callToGoogle(GoogleToken googleToken, Event eventEntity, GoogleTokenRepository googleTokenRepository) throws GeneralSecurityException, IOException {
        InputStream in = GoogleClient.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Файл не найден: " + CREDENTIALS_FILE_PATH);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        return new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new JpaDataStoreFactory(googleTokenRepository))
                .setAccessType("offline")
                .build();
    }

    public void saveCredentialsFromCallback(String authorizationCode, Event event, GoogleTokenRepository googleTokenRepository, GoogleToken googleToken) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        InputStream in = GoogleClient.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Файл не найден: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new JpaDataStoreFactory(googleTokenRepository))
                .setAccessType("offline")
                .build();

        GoogleTokenResponse response = flow.newTokenRequest(authorizationCode)
                .setRedirectUri("http://localhost:8080/api/v1/google/auth/callback?event=" + event.getId())
                .execute();

        Credential credential = flow.createAndStoreCredential(response, String.valueOf(event.getOwner().getId()));

        googleToken.setAccessToken(credential.getAccessToken());
        googleToken.setRefreshToken(credential.getRefreshToken());
        googleToken.setExpirationTimeMilliseconds(credential.getExpirationTimeMilliseconds());
        googleToken.setOauthClientSecret(clientSecrets.getDetails().getClientSecret());
        googleToken.setOauthClientId(clientSecrets.getDetails().getClientId());
    }

    public GoogleAuthorizationCodeRequestUrl getAuthorizationUrl(Event eventEntity, GoogleAuthorizationCodeFlow flow) {
        return flow.newAuthorizationUrl()
                .setAccessType("offline")
                .setRedirectUri("http://localhost:8080/api/v1/google/auth/callback?event=" + eventEntity.getId());
    }

    public com.google.api.services.calendar.model.Event sendEventToCalendar(Event eventEntity, GoogleToken googleToken) throws IOException, GeneralSecurityException {
        InputStream in = GoogleClient.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = new GoogleCredential.Builder()
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(clientSecrets)
                .build();
        credential.setAccessToken(googleToken.getAccessToken());
        credential.setRefreshToken(googleToken.getRefreshToken());
        credential.setExpirationTimeMilliseconds(googleToken.getExpirationTimeMilliseconds());

        Calendar service =
                new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                        .setApplicationName(APPLICATION_NAME)
                        .build();

        com.google.api.services.calendar.model.Event event = initEvent(eventEntity);
        String calendarId = "primary";
        event = service.events().insert(calendarId, event).execute();
        return event;
    }

    public com.google.api.services.calendar.model.Event initEvent(Event eventEntity) {
        com.google.api.services.calendar.model.Event event = new com.google.api.services.calendar.model.Event()
                .setSummary(eventEntity.getTitle())
                .setLocation(eventEntity.getLocation())
                .setDescription(eventEntity.getDescription());

        ZoneOffset zoneOffset = ZoneOffset.UTC;
        ZonedDateTime zonedStart = ZonedDateTime.of(eventEntity.getStartDate(), zoneOffset);
        ZonedDateTime zonedEnd = ZonedDateTime.of(eventEntity.getEndDate(), zoneOffset);
        DateTimeFormatter rfc3339Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        String formattedStart = rfc3339Formatter.format(zonedStart);
        String formattedEnd = rfc3339Formatter.format(zonedEnd);

        DateTime startDateTime = new DateTime(formattedStart);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("UTC");
        event.setStart(start);

        DateTime endDateTime = new DateTime(formattedEnd);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("UTC");
        event.setEnd(end);

        return event;
    }
}

