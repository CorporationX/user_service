package school.faang.user_service.service.event;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.google.CalendarEventDto;
import school.faang.user_service.dto.google.GoogleEventResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.BadRequestException;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.mapper.event.CalendarMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.google.GoogleTokenRepository;
import school.faang.user_service.util.GoogleConfig;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {
    private final GoogleTokenRepository googleTokenRepository;
    private final EventRepository eventRepository;
    private final CalendarMapper calendarMapper;
    private final GoogleConfig googleConfig;
    private final UserRepository userRepository;


    public GoogleEventResponseDto createEvent(Long userId, Long eventId) throws IOException, GeneralSecurityException {
        school.faang.user_service.entity.event.Event newEvent = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.getParticipatedEvents().contains(newEvent)) {
            throw new BadRequestException("User with id " + userId + " is not part of event with id " + eventId);
        }

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        if (!googleTokenRepository.existsByUser(user)) {
            return GoogleEventResponseDto.builder()
                    .message("Follow the link to authorize your calendar")
                    .link(googleConfig.getAuthorizationLink(HTTP_TRANSPORT, userId, eventId))
                    .build();
        }
        CalendarEventDto eventDto = calendarMapper.toDto(newEvent);
        Event event = googleConfig.createEvent(eventDto);
        Calendar service = googleConfig.createService(user);
        Event result = service.events().insert("primary", event).execute();
        return googleConfig.getResponse(result.getHtmlLink());
    }

    public GoogleEventResponseDto handleCallback(String code, Long userId) throws IOException, GeneralSecurityException {
        googleConfig.callBack(code, userId);
        return GoogleEventResponseDto.builder().message("User authorized").build();
    }
}