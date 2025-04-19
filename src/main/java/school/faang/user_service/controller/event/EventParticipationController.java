package school.faang.user_service.controller.event;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Validated
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    public void registerParticipant(@Min(1) long eventId, @Min(1) long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    public void unregisterParticipant(@Min(1) long eventId, @Min(1) long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    public List<UserDto> getParticipants(@Min(1) long eventId) {
        return eventParticipationService.getParticipants(eventId);
    }

    public int getParticipantsCount(@Min(1) long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }

}
