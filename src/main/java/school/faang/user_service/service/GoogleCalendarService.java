package school.faang.user_service.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;

import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class GoogleCalendarService {
    private final EventRepository eventRepository;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/google-calendar-credentials.json";


    public String createCalendarEvent(Long eventId) throws IOException, GeneralSecurityException {
        Event eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException(
                        MessageFormat.format("Event {0} not found", eventId)));

        GoogleAuthorizationCodeFlow flow = getFlow();

        return getRedirectionUrl(eventEntity.getId(), flow).build();
   }

    private static GoogleAuthorizationCodeRequestUrl getRedirectionUrl(Long eventId, GoogleAuthorizationCodeFlow flow) {
        return flow.newAuthorizationUrl()
                .setAccessType("offline")
                .setRedirectUri("http://localhost:8080/api/v1/calendar/auth/callback?event=" + eventId);
    }

    public void getCredentialsFromCallback(String code, Long eventId) throws IOException, GeneralSecurityException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException(
                MessageFormat.format("Event {0} not found", eventId)));

        GoogleAuthorizationCodeFlow flow = getFlow();
    }

    private GoogleAuthorizationCodeFlow getFlow() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("File not found " + CREDENTIALS_FILE_PATH);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        return new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
    }}
