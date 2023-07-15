package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventParticipationService;

@Component
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    public void validate(Long eventId, Long userId) {
        if (eventId == null || userId == null) {
            throw new IllegalArgumentException("Cannot use null for event or user ID!");
        }
        if (eventId <= 0 || userId <= 0) {
            throw new IllegalArgumentException("Cannot use 0 or negative number for event or user ID!");
        }
    }

    public void validateEventID(Long eventId) {
        if (eventId == null || eventId <= 0) {
            throw new IllegalArgumentException("Cannot use 0 or negative number for event ID!");
        }
    }
}
