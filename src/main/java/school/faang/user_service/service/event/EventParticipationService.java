package school.faang.user_service.service.event;

import lombok.Data;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@Data
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;

    public EventParticipationService(EventParticipationRepository eventParticipationRepository) {
        this.eventParticipationRepository = eventParticipationRepository;
    }

    public void registerParticipant(long eventId, long userId) {
        if (!eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)) {
            eventParticipationRepository.register(eventId, userId);
        } else {
            throw new IllegalStateException("User is already registered for this event.");
        }
    }

    public void unregisterParticipant(long eventId, long userId) {
        if (!eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new IllegalArgumentException("Пользователь не был зарегистрирован на это событие");
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<User> getAllParticipantsByEventId(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }
}