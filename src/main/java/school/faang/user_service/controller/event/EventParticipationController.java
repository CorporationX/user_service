package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    public UserDto registerParticipant(long eventId, long userId) {
        return eventParticipationService.registerParticipant(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    public List<UserDto> getParticipants(long eventId) {
        return eventParticipationService.getParticipants(eventId);
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}

