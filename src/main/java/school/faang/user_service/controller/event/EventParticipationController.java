package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.event.EventParticipationService;

@RestController
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService service;

    public int getParticipantsCount(long eventId){
        return service.getParticipantsCount(eventId);
    }
}
