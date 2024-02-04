package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.event.EventParticipationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/{eventId}/{userId}")
    public void register(@PathVariable long eventId, @PathVariable long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

}
