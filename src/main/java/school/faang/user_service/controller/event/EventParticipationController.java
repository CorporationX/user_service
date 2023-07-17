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

    public int getParticipantsCount(long eventId) {
        validateEventId(eventId);
        return eventParticipationService.getParticipantsCount(eventId);
    }


    private void validateParams(long eventId, long userId) {
        if (eventId < 0 || userId < 0) {
            throw new DataValidationException("Id must be more than zero");
        }
    }

    private static void validateEventId(long eventId) {
        if (eventId < 0) {
            throw new IllegalArgumentException("Event not found");
        }
    }
}
