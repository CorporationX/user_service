package school.faang.user_service.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.GoogleToken;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.GoogleTokenRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.util.GoogleClient;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {
    private final EventRepository eventRepository;
    private final GoogleTokenRepository googleTokenRepository;
    private final GoogleClient googleClient;

    @Transactional
    public String createEvent(Long eventId) throws GeneralSecurityException, IOException {
        Event eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Событие не найдено"));
        GoogleToken googleToken = googleTokenRepository.findByUser(eventEntity.getOwner());

        GoogleAuthorizationCodeFlow flow = googleClient.callToGoogle(googleToken, eventEntity, googleTokenRepository);

        if (googleToken != null && googleToken.getAccessToken() != null) {
            return googleClient.sendEventToCalendar(eventEntity, googleToken).getHtmlLink();
        } else {
            return googleClient.getAuthorizationUrl(eventEntity, flow).build();
        }
    }

    @Transactional
    public void saveCredentialsFromCallback(String authorizationCode, Long eventId) throws IOException, GeneralSecurityException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Event is not found"));
        GoogleToken googleToken = new GoogleToken();
        googleClient.saveCredentialsFromCallback(authorizationCode, event, googleTokenRepository, googleToken);
        googleToken.setUuid(UUID.randomUUID().toString());
        googleToken.setUser(event.getOwner());
        googleToken.setUpdatedAt(LocalDateTime.now());
        googleTokenRepository.save(googleToken);
    }
}
