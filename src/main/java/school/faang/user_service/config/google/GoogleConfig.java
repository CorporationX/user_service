package school.faang.user_service.config.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.dto.google.CalendarEventDto;
import school.faang.user_service.dto.google.GoogleCalendarPojo;
import school.faang.user_service.dto.google.GoogleEventResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.google.GoogleTokenRepository;
import school.faang.user_service.util.JpaDataStoreFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Configuration
public class GoogleConfig {
    private final GoogleCalendarPojo calendarPojo;
    private final GoogleTokenRepository googleTokenRepository;

    private static final NetHttpTransport HTTP_TRANSPORT;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void callBack(String code, Long userId) throws IOException {
        GoogleClientSecrets clientSecrets = getClientSecrets();
        GoogleAuthorizationCodeFlow flow = getFlow(clientSecrets);

        TokenResponse response = flow.newTokenRequest(code)
                .setRedirectUri(calendarPojo.getRedirectUri())
                .execute();
        flow.createAndStoreCredential(response, String.valueOf(userId));
    }

    public Calendar createService(User user) throws IOException {

        GoogleClientSecrets clientSecrets = getClientSecrets();
        GoogleAuthorizationCodeFlow flow = getFlow(clientSecrets);
        Credential credential = flow.loadCredential(String.valueOf(user.getId()));
        return getService(credential);
    }

    public String getAuthorizationLink(Long userId, Long eventId)
            throws IOException {
        GoogleClientSecrets clientSecrets = getClientSecrets();
        GoogleAuthorizationCodeFlow flow = getFlow(clientSecrets);

        return flow.newAuthorizationUrl()
                .setRedirectUri(calendarPojo.getRedirectUri())
                .setState(userId + "-" + eventId)
                .build();
    }

    public GoogleClientSecrets getClientSecrets() throws IOException {
        InputStream in = GoogleConfig.class.getResourceAsStream(calendarPojo.getCredentialsFile());
        if (in == null) {
            throw new FileNotFoundException("File not found: " + calendarPojo.getCredentialsFile());
        }
        return GoogleClientSecrets.load(calendarPojo.getJsonFactory(), new InputStreamReader(in));
    }

    public GoogleAuthorizationCodeFlow getFlow(GoogleClientSecrets clientSecrets)
            throws IOException {
        return new GoogleAuthorizationCodeFlow
                .Builder(HTTP_TRANSPORT, calendarPojo.getJsonFactory(), clientSecrets, calendarPojo.getScopes())
                .setDataStoreFactory(new JpaDataStoreFactory(googleTokenRepository))
                .setAccessType(calendarPojo.getAccessType())
                .build();
    }

    public GoogleEventResponseDto getResponse(String link) {
        return GoogleEventResponseDto.builder()
                .message("The event has been successfully added to your Google calendar.")
                .link(link)
                .build();
    }

    public Calendar getService(Credential credential) {
        return new Calendar.Builder(HTTP_TRANSPORT, calendarPojo.getJsonFactory(), credential)
                .setApplicationName(calendarPojo.getApplicationName())
                .build();
    }

    public Event createEvent(CalendarEventDto eventDto) {
        Event event = new Event();
        event.setDescription(eventDto.getDescription());
        event.setSummary(eventDto.getTitle());
        event.setLocation(eventDto.getLocation());

        LocalDateTime localDateTime = eventDto.getStartDate();
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        DateTime dataStart = new DateTime(zonedDateTime.toInstant().toEpochMilli());

        EventDateTime eventDateTime = new EventDateTime()
                .setDateTime(dataStart);
        event.setStart(eventDateTime);

        LocalDateTime localDateTime1 = eventDto.getEndDate();
        ZonedDateTime zonedDateTime1 = localDateTime1.atZone(ZoneId.systemDefault());
        DateTime dataEnd = new DateTime(zonedDateTime1.toInstant().toEpochMilli());

        EventDateTime eventDateTime2 = new EventDateTime()
                .setDateTime(dataEnd);
        event.setEnd(eventDateTime2);
        return event;
    }
}