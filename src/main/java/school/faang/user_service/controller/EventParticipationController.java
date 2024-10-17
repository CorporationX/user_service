package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.service.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @GetMapping("/{eventId}/participants/count")
    public ResponseEntity<Integer> getParticipantsCount(long eventId) {
        int count = eventParticipationService.getParticipantsCount(eventId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{eventId}/participants")
    public List<User> getParticipant(long eventId) {
        return eventParticipationService.getParticipant(eventId);
    }

    @PostMapping("/{eventId}/participants/{userId}")
    public ResponseEntity<String> registerParticipant(@PathVariable long eventId, @PathVariable long userId) {
        try {
            eventParticipationService.manageParticipation(eventId, userId, true);
            return ResponseEntity.ok("User successfully registered");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{eventId}/participants/{userId}")
    public ResponseEntity<String> unregisterParticipant(@PathVariable long eventId, @PathVariable long userId) {
        try {
            eventParticipationService.manageParticipation(eventId, userId, false);
            return ResponseEntity.ok("User successfully unregistered");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}