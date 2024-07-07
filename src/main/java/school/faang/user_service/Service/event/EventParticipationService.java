package school.faang.user_service.Service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
public class EventParticipationService {

    @Autowired
    private EventParticipationRepository eventParticipationRepository;

    public void registrParticipant(long userId , long eventId){
        if(eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .anyMatch(user -> user.getId() == userId)){
            throw new RuntimeException("User is already registered for the event.");
        } else {
            eventParticipationRepository.register(eventId , userId);
        }
    }

    public void unregisterParticipant(long userId , long eventId){
        if(eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .anyMatch(user -> user.getId() == userId)){
            eventParticipationRepository.unregister(eventId , userId);
        } else {
            throw new RuntimeException("User is not registered for the event.");
        }
    }

    public List<User> getPaticipant(long eventId){
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    public int getParticipantCount(long eventId){
    return eventParticipationRepository.countParticipants(eventId);
    }
}
