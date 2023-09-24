package school.faang.user_service.client;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.GoogleCalendarService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class GoogleCalendarClient {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String APP_NAME = "Google Calendar API";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CREDENTIALS_FILE_PATH = "/google-calendar-credentials.json";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private NetHttpTransport httpTransport;

    @Bean
    public GoogleAuthorizationCodeFlow getFlow() throws IOException{
        GoogleClientSecrets googleClientSecrets = new GoogleClientSecrets();

        try (InputStream in = this.getClass().getResourceAsStream(CREDENTIALS_FILE_PATH)) {
            if (Objects.isNull(in)) {
                throw new FileNotFoundException("File not found: " + CREDENTIALS_FILE_PATH);
            }
            googleClientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        } catch (IOException e) {
            log.error("Failed to load client secrets", e);
        }

        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            log.error("Failed to initialize GoogleNetHttpTransport", e);
        }
        return new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, googleClientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
    }

    private NetHttpTransport getHttpTransport() throws GeneralSecurityException, IOException {
        return GoogleNetHttpTransport.newTrustedTransport();
    }

    public GoogleClientSecrets getClientSecrets() throws IOException {
        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("File not found: " + CREDENTIALS_FILE_PATH);
        }

        return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
    }

    public Credential getCredential() throws GeneralSecurityException, IOException {
        return new GoogleCredential.Builder()
                .setTransport(getHttpTransport())
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(getClientSecrets())
                .build();
    }

    public Calendar getCalendar() throws GeneralSecurityException, IOException {
        return new Calendar.Builder(getHttpTransport(), JSON_FACTORY, getCredential())
                .setApplicationName(APP_NAME)
                .build();
    }
}
