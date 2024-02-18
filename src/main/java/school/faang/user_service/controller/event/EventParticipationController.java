package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/events")
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/register/{eventId}/{userId}")
    public void registerParticipant(@PathVariable long eventId, @PathVariable long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @PostMapping("/unregister/{eventId}/{userId}")
    public void unregisterParticipant(@PathVariable long eventId, @PathVariable long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/participants/{eventId}")
    public List<UserDto> getParticipant(@PathVariable long eventId) {
        return eventParticipationService.getParticipant(eventId);
    }

    @GetMapping("/participants/count/{eventId}")
    public int getParticipantsCount(@PathVariable long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }

}
