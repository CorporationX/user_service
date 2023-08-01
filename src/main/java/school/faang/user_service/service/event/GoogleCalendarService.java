package school.faang.user_service.service.event;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.google.CalendarEventDto;
import school.faang.user_service.dto.google.GoogleCalendarPojo;
import school.faang.user_service.dto.google.GoogleEventResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.mapper.event.CalendarMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
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

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

    private final GoogleTokenRepository googleTokenRepository;
    private final EventRepository eventRepository;
    private final CalendarMapper calendarMapper;
    private final GoogleCalendarPojo calendarPojo;
    private final UserRepository userRepository;

    public GoogleEventResponseDto createEvent(Long eventId, Long userId) throws IOException, GeneralSecurityException {
        school.faang.user_service.entity.event.Event newEvent = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        CalendarEventDto eventDto = calendarMapper.toDto(newEvent);

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

//        if (!user.getParticipatedEvents().contains(newEvent)) {
//            throw new DataValidationException("User with id " + userId + " is not part of event with id " + eventId);
//        }

        Event event = createEvent(eventDto);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        if (!googleTokenRepository.existsByUser(user)) {

            return GoogleEventResponseDto.builder()
                    .message("Follow the link to authorize your calendar")
                    .link(getAuthorizationLink(HTTP_TRANSPORT, userId, eventId))
                    .build();
        }

        GoogleClientSecrets clientSecrets = getClientSecrets();
        GoogleAuthorizationCodeFlow flow = getFlow(HTTP_TRANSPORT, clientSecrets);
        Credential credential = flow.loadCredential(String.valueOf(userId));
        Calendar service = getService(HTTP_TRANSPORT, credential);

        Event result = service.events().insert("primary", event).execute();

        return getResponse(result.getHtmlLink());
    }

    private String getAuthorizationLink(NetHttpTransport HTTP_TRANSPORT, Long userId, Long eventId)
            throws IOException {
        GoogleClientSecrets clientSecrets = getClientSecrets();
        GoogleAuthorizationCodeFlow flow = getFlow(HTTP_TRANSPORT, clientSecrets);

        // Returns authorization url
        return flow.newAuthorizationUrl()
                .setRedirectUri(calendarPojo.getRedirectUri())
                .setState(userId + "-" + eventId) // Не получилось придумать как еще передать параметры
                .build();
    }

    private GoogleClientSecrets getClientSecrets() throws IOException {
        InputStream in = GoogleCalendarService.class.getResourceAsStream(calendarPojo.getCredentialsFile());
        if (in == null) {
            throw new FileNotFoundException("File not found: " + calendarPojo.getCredentialsFile());
        }
        return GoogleClientSecrets.load(calendarPojo.getJsonFactory(), new InputStreamReader(in));
    }

    private GoogleAuthorizationCodeFlow getFlow(HttpTransport HTTP_TRANSPORT, GoogleClientSecrets clientSecrets)
            throws IOException {
        return new GoogleAuthorizationCodeFlow
                .Builder(HTTP_TRANSPORT, calendarPojo.getJsonFactory(), clientSecrets, calendarPojo.getScopes())
                .setDataStoreFactory(new JpaDataStoreFactory(googleTokenRepository))
                .setAccessType(calendarPojo.getAccessType())
                .build();
    }

    private GoogleEventResponseDto getResponse(String link) {
        return GoogleEventResponseDto.builder()
                .message("The event has been successfully added to your Google calendar.")
                .link(link)
                .build();
    }

    private Calendar getService(HttpTransport HTTP_TRANSPORT, Credential credential) {
        return new Calendar.Builder(HTTP_TRANSPORT, calendarPojo.getJsonFactory(), credential)
                .setApplicationName(calendarPojo.getApplicationName())
                .build();
    }


    private Event createEvent(CalendarEventDto eventDto) {
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
