package school.faang.user_service.config.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.GoogleTokenRepository;
import school.faang.user_service.util.google.JpaDataStoreFactory;


import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import static com.google.api.client.json.gson.GsonFactory.getDefaultInstance;

@Data
@Component
@RequiredArgsConstructor
public class GoogleConfig {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${google.calendar.application-name}")
    private String applicationName;

    @Value("${google.calendar.credentials-file-path}")
    private String credentialsFilePath;

    @Value("${google.calendar.redirect-uri}")
    private String redirectUri;

    @Value("${google.calendar.access-type}")
    private String accessType;

    @Value("${google.calendar.calendar-id}")
    private String calendarId;

    private final GoogleTokenRepository googleTokenRepository;

    public GoogleAuthorizationCodeFlow getFlow()
            throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = getDefaultInstance();
        return new GoogleAuthorizationCodeFlow
                .Builder(HTTP_TRANSPORT, jsonFactory, getClientSecrets(), Collections.singleton(CalendarScopes.CALENDAR))
                .setDataStoreFactory(new JpaDataStoreFactory(googleTokenRepository))
                .setAccessType(accessType)
                .build();
    }

    public Calendar getService(Credential credential) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = getDefaultInstance();
        return new Calendar.Builder(HTTP_TRANSPORT, jsonFactory, credential)
                .setApplicationName(applicationName)
                .build();
    }

    public String getAuthorizationLink(Long userId, Long eventId)
            throws IOException, GeneralSecurityException {
        GoogleAuthorizationCodeFlow flow = getFlow();

        return flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setState(userId + "-" + eventId)
                .build();
    }

    private GoogleClientSecrets getClientSecrets() throws IOException {
        JsonFactory jsonFactory = getDefaultInstance();
        return GoogleClientSecrets.load(jsonFactory,
                new FileReader(credentialsFilePath));
    }
}
