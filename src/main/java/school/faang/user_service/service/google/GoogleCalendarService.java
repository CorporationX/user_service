package school.faang.user_service.service.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.services.calendar.Calendar;

import java.io.IOException;
import java.security.GeneralSecurityException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.google.GoogleConfig;
import school.faang.user_service.config.google.GoogleProperties;
import school.faang.user_service.dto.event.GoogleEventDto;
import school.faang.user_service.dto.event.GoogleEventResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EventNotFoundException;
import school.faang.user_service.exception.NotPartOfEventException;
import school.faang.user_service.mapper.event.GoogleEventDtoMapper;
import school.faang.user_service.mapper.event.GoogleEventMapper;
import school.faang.user_service.repository.GoogleTokenRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {
    private final GoogleTokenRepository googleTokenRepository;
    private final EventRepository eventRepository;
    private final GoogleEventDtoMapper googleEventDtoMapper;
    private final GoogleEventMapper googleEventMapper;
    private final GoogleConfig googleConfig;
    private final GoogleProperties googleProperties;
    private final UserRepository userRepository;

    @Transactional
    public GoogleEventResponseDto createEvent(Long userId, Long eventId) throws GeneralSecurityException, IOException {
        school.faang.user_service.entity.event.Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("This user was not found"));

        if (!user.getParticipatedEvents().contains(event)) {
            throw new NotPartOfEventException("User with id " + userId + " is not part of event with id " + eventId);
        }

        if (!googleTokenRepository.existsByUser(user)) {
            return GoogleEventResponseDto.builder()
                    .message("Follow the link to authorize your calendar")
                    .link(googleConfig.getAuthorizationLink(userId, eventId))
                    .build();
        }

        GoogleAuthorizationCodeFlow flow = googleConfig.getFlow();
        Credential credential = flow.loadCredential(String.valueOf(userId));
        Calendar service = googleConfig.getService(credential);

        GoogleEventDto googleEventDto = googleEventDtoMapper.toGoogleEventDto(event);
        com.google.api.services.calendar.model.Event googleEvent = googleEventMapper.toGoogleEvent(googleEventDto);
        googleEvent = service.events().insert(googleProperties.getCalendarId(), googleEvent).execute();

        return getResponse(googleEvent.getHtmlLink());
    }

    @Transactional
    public GoogleEventResponseDto handleCallback(String code, Long userId, Long eventId)
            throws GeneralSecurityException, IOException {
        school.faang.user_service.entity.event.Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        GoogleAuthorizationCodeFlow flow = googleConfig.getFlow();

        TokenResponse response = flow.newTokenRequest(code)
                .setRedirectUri(googleProperties.getRedirectUri())
                .execute();
        Credential credential = flow.createAndStoreCredential(response, String.valueOf(userId));
        Calendar service = googleConfig.getService(credential);

        GoogleEventDto googleEventDto = googleEventDtoMapper.toGoogleEventDto(event);
        com.google.api.services.calendar.model.Event googleEvent = googleEventMapper.toGoogleEvent(googleEventDto);
        googleEvent = service.events().insert(googleProperties.getCalendarId(), googleEvent).execute();

        return getResponse(googleEvent.getHtmlLink());
    }

    private GoogleEventResponseDto getResponse(String link) {
        return GoogleEventResponseDto.builder()
                .message("The event has been successfully added to your Google calendar.")
                .link(link)
                .build();
    }
}