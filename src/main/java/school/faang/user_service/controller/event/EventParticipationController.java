package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService service;

    public List<User> getParticipant(long eventId){
        return service.getParticipant(eventId);
    }
}
