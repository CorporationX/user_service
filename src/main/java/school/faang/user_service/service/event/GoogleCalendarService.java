package school.faang.user_service.service.event;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.google.GoogleConfig;
import school.faang.user_service.dto.google.CalendarEventDto;
import school.faang.user_service.dto.google.GoogleEventResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.BadRequestException;
import school.faang.user_service.mapper.event.CalendarMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.google.GoogleTokenRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {
    private final GoogleTokenRepository googleTokenRepository;
    private final EventRepository eventRepository;
    private final CalendarMapper calendarMapper;
    private final GoogleConfig googleConfig;
    private final UserRepository userRepository;


    public GoogleEventResponseDto createEvent(Long userId, Long eventId) throws IOException {
        school.faang.user_service.entity.event.Event newEvent = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!user.getParticipatedEvents().contains(newEvent)) {
            throw new BadRequestException("User with id " + userId + " is not part of event with id " + eventId);
        }

        if (!googleTokenRepository.existsByUser(user)) {
            return GoogleEventResponseDto.builder()
                    .message("Follow the link to authorize your calendar")
                    .link(googleConfig.getAuthorizationLink(userId, eventId))
                    .build();
        }
        CalendarEventDto eventDto = calendarMapper.toDto(newEvent);
        Event event = googleConfig.createEvent(eventDto);
        Calendar service = googleConfig.createService(user);
        Event result = service.events().insert("primary", event).execute();
        return googleConfig.getResponse(result.getHtmlLink());
    }

    public GoogleEventResponseDto handleCallback(String code, Long userId) throws IOException {
        googleConfig.callBack(code, userId);
        return GoogleEventResponseDto.builder().message("User authorized").build();
    }
}