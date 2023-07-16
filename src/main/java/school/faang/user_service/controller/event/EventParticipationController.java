package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

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

    public List<UserDto> getListOfParticipantController(Long eventId) {
        validateEventID(eventId);
        return eventParticipationService.getListOfParticipant(eventId);
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