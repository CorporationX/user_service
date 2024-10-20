package school.faang.user_service.service.event;

import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
public class EventParticipationService {
    // add constructor
    public EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(long userId, long eventId){
        eventParticipationRepository.register(userId,eventId);
    }
    public void unregisterParticipant(long userId, long eventId){
        eventParticipationRepository.unregister(userId, eventId);
    }
    public List<User> getParticipant(long eventId){
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }
    public int getParticipantsCount(long eventId){
        return eventParticipationRepository.countParticipants(eventId);
    }
}
