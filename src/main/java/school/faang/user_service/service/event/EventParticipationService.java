package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public void registerParticipant(long eventId, long userId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);

        validatePossibility(user.getId(), event.getId());

        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        validateUnregisterPossibility(eventId, userId);

        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<User> getParticipants(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    private Event getEvent(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Event not found"));
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private void validatePossibility(long userId, long eventId) {
        if (isUserExist(userId, eventId)) {
            throw new IllegalArgumentException("User already registered");
        }
    }

    private void validateUnregisterPossibility(long eventId, long userId) {
        if (!isUserExist(userId, eventId)) {
            throw new IllegalArgumentException("User not registered");
        }
    }

    private boolean isUserExist(long userId, long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream().anyMatch(u -> u.getId() == userId);
    }
}
