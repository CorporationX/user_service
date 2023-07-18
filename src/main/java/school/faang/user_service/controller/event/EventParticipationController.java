package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventParticipationService;

@RequiredArgsConstructor
@Controller
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    public void registerParticipant(long eventId, long userId) {
        validateParams(eventId, userId);
        eventParticipationService.registerParticipant(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        validateParams(userId, eventId);

        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    private void validateParams(long eventId, long userId) {
        if (eventId < 0 || userId < 0) {
            throw new DataValidationException("Id must be more than zero");
        }
    }
}
