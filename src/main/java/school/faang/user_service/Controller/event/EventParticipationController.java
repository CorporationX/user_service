package school.faang.user_service.controller.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import school.faang.user_service.service.event.EventParticipationService;
import school.faang.user_service.dto.UserDto;

import java.util.List;

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

    public List<UserDto> getParticipant(long eventId){
        return eventParticipationService.getPaticipant(eventId);
    }

    public int getParticipantCount(long eventId){
        return eventParticipationService.getParticipantCount(eventId);
    }

}
