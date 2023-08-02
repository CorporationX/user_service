package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventParticipationService;

@Controller
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    public void registerParticipant(Long eventId, Long userId) {
        validate(eventId, userId);
        eventParticipationService.registerParticipant(eventId, userId);
    }

    public void unregisterParticipant(Long eventId, Long userId) {
        validate(eventId, userId);
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    public void validate(Long eventId, Long userId) {
        if (eventId == null || userId == null) {
            throw new DataValidationException("Cannot use null for event of user ID!");
        }
    }
}