package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("v1/events")
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/register")
    public void registerParticipant(@RequestParam("eventId") Long eventId,
                                    @RequestParam("userId")  Long userId) {
         eventParticipationService.registerParticipant(eventId, userId);
    }

    @DeleteMapping("/unregister")
    public ResponseEntity<Void> unregisterParticipant(@RequestParam("eventId") Long eventId,
                                                      @RequestParam("userId")  Long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/participants")
    public List<UserDto> getParticipant(@RequestParam("eventId") Long eventId) {
        return eventParticipationService.getParticipant(eventId);
    }

    @GetMapping("/{eventId}/participants/count")
    public int getParticipantCount(@PathVariable Long eventId) {
        return eventParticipationService.getParticipantCount(eventId);
    }
}
