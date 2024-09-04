package school.faang.user_service.controller.event;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.EventUserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService participationService;

    public void registerParticipant(@Positive long eventId, @Positive long userId) {
        participationService.registerParticipant(eventId, userId);

    }

    public void unregisterParticipant(@Positive long eventId, @Positive long userId) {
        participationService.unregisterParticipant(eventId, userId);
    }

    public List<EventUserDto> getParticipants(@Positive long eventId) {
        return participationService.getParticipants(eventId);
    }

    public Integer getParticipantsCount(@Positive long eventId) {
        return participationService.getParticipantsCount(eventId);
    }
}
