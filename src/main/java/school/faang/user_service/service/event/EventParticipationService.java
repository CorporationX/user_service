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
        User user = userRepository.findById(userId).orElse(null);
        Event event = eventRepository.findById(eventId).orElse(null);

        validateParams(user, event);

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

    private void validatePossibility(long userId, long eventId) {
        boolean exist = eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream().anyMatch(u -> u.getId() == userId);
        if (exist) {
            throw new IllegalArgumentException("User already registered");
        }
    }

    private void validateUnregisterPossibility(long eventId, long userId) {
        if (findAlreadyRegisteredUser(eventId, userId) == null) {
            throw new IllegalArgumentException("User not registered");
        }
    }

    private User findAlreadyRegisteredUser(long eventId, long userId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream().filter(user -> user.getId() == userId).findFirst().orElse(null);
    }

    private static void validateParams(User user, Event event) {
        if (user == null) {
            throw new NullPointerException("User not found");
        }
        if (event == null) {
            throw new NullPointerException("Event not found");
        }
    }
}
