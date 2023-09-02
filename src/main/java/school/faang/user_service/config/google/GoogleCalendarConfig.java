package school.faang.user_service.config.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.util.google.JpaDataStoreFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;

@Data
@Configuration
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarConfig {
    @Value("${spring.google.calendar.application-name}")
    private String applicationName;
    @Value("${spring.google.calendar.credentials-file-path}")
    private String credentialsFile;
    @Value("${spring.google.calendar.calendar-id}")
    private String calendarId;
    @Value("${spring.google.calendar.redirect-uri}")
    private String redirectUri;
    @Value("${spring.google.calendar.scopes}")
    private List<String> scopes;
    @Value("${spring.google.calendar.access-type}")
    private String accessType;
    private JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    private NetHttpTransport httpTransport;
    private final JpaDataStoreFactory jpaDataStoreFactory;

    @Bean
    public GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow() throws IOException {
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
        try (InputStream in = this.getClass().getResourceAsStream("/credentials.json")) {
            if (Objects.isNull(in)) {
                throw new FileNotFoundException("File not found: " + credentialsFile);
            }
            clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));
        } catch (IOException e) {
            log.error("Failed to load client secrets", e);
        }

        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            log.error("Failed to initialize GoogleNetHttpTransport", e);
        }

        return new GoogleAuthorizationCodeFlow
                .Builder(httpTransport, jsonFactory, clientSecrets, scopes)
                .setDataStoreFactory(jpaDataStoreFactory)
                .setAccessType(accessType)
                .build();
    }

    @Bean
    public NetHttpTransport httpTransport() {
        if (Objects.isNull(httpTransport)) {
            try {
                httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            } catch (GeneralSecurityException | IOException e) {
                log.error("Failed to initialize GoogleNetHttpTransport", e);
            }
        }
        return httpTransport;
    }
}
