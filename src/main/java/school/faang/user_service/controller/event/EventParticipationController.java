package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

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

    public List<User> getParticipants(long eventId) {
        validateEventId(eventId);
        return eventParticipationService.getParticipants(eventId);
    }

    public int getParticipantsCount(long eventId) {
        validateEventId(eventId);
        return eventParticipationService.getParticipantsCount(eventId);
    }

    private void validateParams(long eventId, long userId) {
        validateEventId(eventId);
        validateUserId(userId);
    }

    private static void validateUserId(long userId) {
        if (userId < 0) {
            throw new IllegalArgumentException("User not found");
        }
    }

    private static void validateEventId(long eventId) {
        if (eventId < 0) {
            throw new IllegalArgumentException("Event not found");
        }
    }
}
