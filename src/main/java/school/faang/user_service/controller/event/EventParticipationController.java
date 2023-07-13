package school.faang.user_service.controller.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventParticipationService;

@Component
public class EventParticipationController {
    private EventParticipationService eventParticipationService;

    @Autowired
    public EventParticipationController(EventParticipationService eventParticipationService) {
        this.eventParticipationService = eventParticipationService;
    }

    public void checkValidate(Long eventId, Long userId) {
        if (eventId == null || userId == null) {
            throw new IllegalArgumentException("Cannot use null for event of user ID!");
        }
    }
}
