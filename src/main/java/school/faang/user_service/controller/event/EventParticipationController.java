package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventParticipationService;

@Component
@RequiredArgsConstructor
public class EventParticipationController { // A

    private EventParticipationService eventParticipationService; // B

    public void validate(Long eventId, Long userId) {
        if (eventId == null || userId == null) {
            throw new IllegalArgumentException("Cannot use null for event of user ID!");
        }
    }
}