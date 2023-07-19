package school.faang.user_service.controller.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@Controller
@AllArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @PostMapping("/{eventId}/register/{userId}")
    public void registerParticipant(Long eventId, Long userId) {
        validate(eventId, userId);
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @PostMapping("/{eventId}/unregister/{userId}")
    public void unregisterParticipant(Long eventId, Long userId) {
        validate(eventId, userId);
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/{eventId}/participants")
    public List<UserDto> getParticipants(Long eventId) {
        validateEventId(eventId);
        return eventParticipationService.getParticipants(eventId);
    }

    @GetMapping("{eventId}/participants/count")
    public int getParticipantsCount(Long eventId) {
        validateEventId(eventId);
        return eventParticipationService.getParticipantsCount(eventId);
    }

    private void validate(Long eventId, Long userId) {
        if (eventId == null || userId == null) {
            throw new DataValidationException("Event id or user id cannot be null");
        }
        if (userId <= 0 || eventId <= 0) {
            throw new DataValidationException("User id cannot be less than or equal to 0");
        }
    }

    private void validateEventId(Long eventId) {
        if (eventId == null || eventId <= 0) {
            throw new DataValidationException("Event id cannot be null and less than or equal to 0");
        }
    }
}
