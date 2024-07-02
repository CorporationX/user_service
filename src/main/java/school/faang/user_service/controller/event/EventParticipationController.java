package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.serice.event.EventParticipationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    public void registerParticipant(long eventId, long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }


    public void unregisterParticipant(long eventId, long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    public List<User> getParticipant(long eventId){
        return eventParticipationService.getParticipant(eventId);
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }


}
