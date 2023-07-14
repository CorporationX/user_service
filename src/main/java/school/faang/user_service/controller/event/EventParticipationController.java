package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventParticipationService;

@Component
@RequiredArgsConstructor
public class EventParticipationController { // A

    private EventParticipationService eventParticipationService; // B


    public void registerParticipantController(Long eventId, Long userId) {
        validate(eventId, userId);
        eventParticipationService.registerParticipant(eventId, userId);
    }

    public void unregisterParticipantController(Long eventId, Long userId) {
        validate(eventId, userId);
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    public void validate(Long eventId, Long userId) throws DataValidationException {
        if (eventId == null || userId == null) {
            throw new DataValidationException("Cannot use null for event of user ID!");
        }
        if (eventId <= 0 || userId <= 0) {
            throw new DataValidationException("Cannot use 0 or negative numbers for event or user ID!");
        }
    }
}