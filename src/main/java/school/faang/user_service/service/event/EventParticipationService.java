package school.faang.user_service.service.event;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.EventRegistrationException;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Component
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventRepository;

    @Transactional
    public void registerParticipant(long eventId, long userId) {
        if (isRegistered(eventId, userId)) {
            throw new EventRegistrationException("user is already registered");
        }
        eventRepository.register(eventId, userId);
    }

    @Transactional
    public void unregisterParticipant(long eventId, long userId) {
        if (!isRegistered(eventId, userId)) {
            throw new EventRegistrationException("user wasn't registered");
        }
        eventRepository.unregister(eventId, userId);
    }

    private boolean isRegistered(long eventId, long userId) {
        return eventRepository.findAllParticipantsByEventId(eventId).stream()
                .anyMatch(e -> userId == e.getId());
    }

    @Transactional(readOnly = true)
    public List<User> getParticipants(long eventId) {
        return eventRepository.findAllParticipantsByEventId(eventId);
    }

    @Transactional(readOnly = true)
    public int getParticipantsCount(long eventId) {
        return eventRepository.countParticipants(eventId);
    }
}
