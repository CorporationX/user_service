package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    public void registerParticipant(long eventId, long userId) {
        validateIds(eventId, userId);
        eventParticipationService.registerParticipant(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        validateIds(userId, eventId);
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    public List<UserDto> getParticipants(long eventId) {
        validateIds(eventId);
        return eventParticipationService.getParticipants(eventId);
    }

    public int getParticipantsCount(long eventId) {
        validateIds(eventId);
        return eventParticipationService.getParticipantsCount(eventId);
    }

    private void validateIds(long... ids) {
        Arrays.stream(ids).forEach(id -> {
            if (id < 0) {
                throw new DataValidationException("Id must be more than zero");
            }
        });
    }
}
