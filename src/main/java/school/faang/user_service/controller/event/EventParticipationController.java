package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.event.EventParticipationService;
import school.faang.user_service.entity.User;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/register")
    public void registerParticipant(@RequestParam long eventId, @RequestParam long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @PostMapping("/unregister")
    public void unregisterParticipant(@RequestParam long eventId, @RequestParam long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/participants")
    public List<User> getParticipant(@RequestParam long eventId) {
        return eventParticipationService.getParticipant(eventId);
    }

    @GetMapping("/participants/count")
    public int getParticipantsCount(@RequestParam long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }

}
