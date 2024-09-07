package school.faang.user_service.service.event;

import java.util.List;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Component
@AllArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventRepository;

    @Transactional
    public void registerParticipant(long eventId, long userId){
        var isAlreadyRegistered =
                eventRepository.findAllParticipantsByEventId(eventId).stream().anyMatch(e -> userId == e.getId());
       if (isAlreadyRegistered){
           throw new IllegalArgumentException("user is already registered");
       }
       eventRepository.register(eventId, userId);
    }

    @Transactional
    public void unregisterParticipant(long eventId, long userId) {
        var isRegistered =
                eventRepository.findAllParticipantsByEventId(eventId).stream().anyMatch(e -> userId == e.getId());
        if (!isRegistered){
            throw new IllegalArgumentException("user wasn't registered");
        }
        eventRepository.unregister(eventId, userId);
    }

    public List<User> getParticipants(long eventId) {
        return eventRepository.findAllParticipantsByEventId(eventId);
    }

    public int getParticipantsCount(long eventId) {
        return eventRepository.countParticipants(eventId);
    }
}
