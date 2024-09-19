package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public void registerParticipant(@RequestParam("eventId") Long eventId,
                                    @RequestParam("userId")  Long userId) {
         eventParticipationService.registerParticipant(eventId, userId);
    }

    @DeleteMapping("/unregister/{eventId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void unregisterParticipant(@PathVariable Long eventId,
                                      @PathVariable Long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/participants/{eventId}")
    public List<UserDto> getParticipant(@PathVariable Long eventId) {
        return eventParticipationService.getParticipant(eventId);
    }

    @GetMapping("/participants/count/{eventId}")
    public int getParticipantCount(@PathVariable Long eventId) {
        return eventParticipationService.getParticipantCount(eventId);
    }
}
