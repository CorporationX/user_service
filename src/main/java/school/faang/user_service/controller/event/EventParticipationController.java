package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("v1/events")
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/{eventId}/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerParticipant(@PathVariable long eventId,
                                    @PathVariable long userId) {
         eventParticipationService.registerParticipant(eventId, userId);
    }

    @DeleteMapping("/{eventId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void unregisterParticipant(@PathVariable long eventId,
                                      @PathVariable long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/participants/{eventId}")
    public List<UserDto> getParticipant(@PathVariable long eventId) {
        return eventParticipationService.getParticipant(eventId);
    }

    @GetMapping("/participants/count/{eventId}")
    public int getParticipantCount(@PathVariable long eventId) {
        return eventParticipationService.getParticipantCount(eventId);
    }
}
