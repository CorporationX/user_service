package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Service
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public void registerParticipant(long eventId, long userId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);

        validateParams(user, event);

        validatePossibility(user.getId(), event.getId());

        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        validateUnregisterPossibility(eventId, userId);

        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<User> getParticipants(long eventId) {
        validateEvent(getEvent(eventId));
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    private Event getEvent(long eventId) {
        return eventRepository.findById(eventId).orElse(null);
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElse(null);
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

    private static void validateParams(User user, Event event) {
        validateUser(user);
        validateEvent(event);
    }

    private static void validateEvent(Event event) {
        if (event == null) {
            throw new NullPointerException("Event not found");
        }
    }

    private static void validateUser(User user) {
        if (user == null) {
            throw new NullPointerException("User not found");
        }
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
