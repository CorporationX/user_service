package school.faang.user_service.controller.event;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.service.event.EventParticipationService;

@Controller
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService service;

    void unregisterParticipant(long eventId, long userId) {
        service.unregisterParticipant(eventId, userId);
    }
}
