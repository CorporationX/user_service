package school.faang.user_service.controller.event;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@AllArgsConstructor
@RestController()
@RequestMapping("/api/v1/events")
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;


    @PutMapping("/{eventId}/register")
    public void registerParticipant(@PathVariable Long eventId, @RequestParam Long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @PutMapping("/{eventId}/unregister")
    public void unregisterParticipant(@PathVariable Long eventId, @RequestParam Long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/{eventId}/participants")
    public List<Long> getParticipant(@PathVariable long eventId) {
        List<User> serviceResult = eventParticipationService.getParticipant(eventId);
        return serviceResult.stream().map(u -> u.getId()).toList();
    }

    @GetMapping("/{eventId}/participants-count")
    public long getParticipantsCount(@PathVariable long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}
