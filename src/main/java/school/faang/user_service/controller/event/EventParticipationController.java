package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RegisterRequest;
import school.faang.user_service.service.event.EventParticipationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @PostMapping(path = "/register")
    public void registerParticipant(@RequestBody RegisterRequest request){
        eventParticipationService.registerParticipant(request.getEventId(), request.getUserId());
    }
}
