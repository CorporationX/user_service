package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.event.EventParticipationService;

@RestController
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService service;

    public void unregisterParticipant(long eventId, long userId) {
        service.unregisterParticipant(eventId, userId);
    }
}
