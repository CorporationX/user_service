package school.faang.user_service.controller.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.event.EventParticipationService;
import school.faang.user_service.service.event.EventParticipationServiceImplementation;

@RestController
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @Autowired
    public EventParticipationController(EventParticipationService eventParticipationService) {
        this.eventParticipationService = eventParticipationService;
    }

    @PostMapping("/{eventId}/unregister/{userId}")
    public void registerParticipant(@PathVariable long eventId, @PathVariable long userId){
        eventParticipationService.registerParticipant(eventId, userId);
    }
}
