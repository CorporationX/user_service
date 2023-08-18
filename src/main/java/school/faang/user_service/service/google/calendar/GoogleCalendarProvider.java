package school.faang.user_service.service.google.calendar;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.GoogleCredential;
import school.faang.user_service.mapper.GoogleCredentialMapper;
import school.faang.user_service.repository.GoogleCredentialRepository;


@Lazy(value = true)
@Component
@Data
public class GoogleCalendarProvider {
    private String applicationName;
    private String tokensDirectoryPath;
    private List<String> scopes;
    private String accessType;
    private String clientEmail;

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private GoogleCredentialRepository googleCredentialRepository;
    private GoogleCredentialMapper googleCredentialMapper;

    @Getter
    private Calendar calendar;

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
        throws IOException {

        GoogleCredential googleCredential = googleCredentialRepository.findByClientEmail(clientEmail);

        GoogleClientSecrets.Details clientSecretsDetails = googleCredentialMapper.toDetails(googleCredential);

        GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setWeb(clientSecretsDetails);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokensDirectoryPath)))
            .setAccessType(accessType)
            .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public GoogleCalendarProvider(
        @Value("${google.calendar.application-name}") String applicationName,
        @Value("${google.calendar.tokens-path}") String tokensDirectoryPath,
        @Value("${google.calendar.scopes}") List<String> scopes,
        @Value("${google.calendar.access-type}") String accessType,
        @Value("${google.calendar.client-email}") String clientEmail,

        GoogleCredentialRepository googleCredentialRepository,
        GoogleCredentialMapper googleCredentialMapper
    ) throws GeneralSecurityException, IOException {
        this.applicationName = applicationName;
        this.tokensDirectoryPath = tokensDirectoryPath;
        this.scopes = scopes;
        this.accessType = accessType;
        this.clientEmail = clientEmail;

        this.googleCredentialRepository = googleCredentialRepository;
        this.googleCredentialMapper = googleCredentialMapper;

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        calendar = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
            .setApplicationName(applicationName)
            .build();
    }
}
