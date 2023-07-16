package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

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

    private void validatePossibility(long userId, long eventId) {
        boolean exist = eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream().anyMatch(u -> u.getId() == userId);
        if (exist) {
            throw new IllegalArgumentException("User already registered");
        }
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
