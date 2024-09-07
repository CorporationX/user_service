package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
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
}
