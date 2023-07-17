package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventParticipationService;

@Controller
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    public void registerParticipantController(Long eventId, Long userId) {
        validate(eventId, userId);
        eventParticipationService.registerParticipant(eventId, userId);
    }

    public void unregisterParticipantController(Long eventId, Long userId) {
        validate(eventId, userId);
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    public int getCountRegisteredParticipantController(Long eventId) {
        validateEventID(eventId);
        return eventParticipationService.getCountRegisteredParticipant(eventId);
    }

    public void validate(Long eventId, Long userId) {
        if (eventId == null || userId == null) {
            throw new DataValidationException("Cannot use null for event or user ID!");
        }
        if (eventId <= 0 || userId <= 0) {
            throw new DataValidationException("Cannot use 0 or negative number for event or user ID!");
        }
    }

    public void validateEventID(Long eventId) {
        if (eventId == null || eventId <= 0) {
            throw new DataValidationException("Cannot use 0 or negative number for event ID!");
        }
    }
}
