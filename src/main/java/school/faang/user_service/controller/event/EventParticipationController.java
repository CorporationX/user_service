package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/register")
    public UserDto registerParticipant(Long eventId, Long userId) {
        return eventParticipationService.registerParticipant(eventId, userId);
    }

    @DeleteMapping("/unregister")
    public void unregisterParticipant(Long eventId, Long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/{eventId}/participants")
    public List<UserDto> getParticipants(Long eventId) {
        return eventParticipationService.getParticipants(eventId);
    }

    @GetMapping("/events/{eventId}/participants/count")
    public int getParticipantsCount(Long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}