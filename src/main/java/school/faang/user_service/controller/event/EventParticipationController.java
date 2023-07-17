package school.faang.user_service.controller.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @Autowired
    public EventParticipationController(EventParticipationService eventParticipationService) {
        this.eventParticipationService = eventParticipationService;
    }

    @PostMapping("/{eventId}/register/{userId} ")
    public void registerParticipant(@PathVariable Long eventId, @PathVariable Long userId){
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @PostMapping("/{eventId}/unregister/{userId} ")
    public void unregisterParticipant(@PathVariable Long eventId, @PathVariable Long userId){
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @PostMapping("/{eventId}/participants")
    public List<User> getParticipant(@PathVariable Long eventID) {
        return eventParticipationService.getParticipant(eventID);
    }
}
