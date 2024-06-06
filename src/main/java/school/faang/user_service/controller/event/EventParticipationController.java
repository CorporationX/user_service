package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/register")
    public UserDto registerParticipant(@RequestParam("eventId") long eventId, @RequestParam("userId") long userId) {
        return eventParticipationService.registerParticipant(eventId, userId);
    }

    @DeleteMapping("/unregister")
    public void unregisterParticipant(@RequestParam("eventId") long eventId, @RequestParam("userId") long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/{eventId}/participants")
    public List<UserDto> getParticipants(@PathVariable("eventId") long eventId) {
        return eventParticipationService.getParticipants(eventId);
    }

    @PostMapping("/{eventId}/participants/count")
    public int getParticipantsCount(@PathVariable("eventId") long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return errorResponse;
    }
}