package school.faang.user_service.Controller.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import school.faang.user_service.Service.event.EventParticipationService;

@Controller
public class EventParticipationController {

    @Autowired
    private EventParticipationService eventParticipationService;

    public void registerParticipant(long userId , long eventId){
        eventParticipationService.registrParticipant(userId , eventId);
    }

    public void unregisterParticipant(long userId , long eventId){
        eventParticipationService.unregisterParticipant(userId , eventId);
    }

    public void getParticipant(long eventId){
        eventParticipationService.getPaticipant(eventId);
    }

    public void getParticipantCount(long eventId){
        eventParticipationService.getParticipantCount(eventId);
    }
}
