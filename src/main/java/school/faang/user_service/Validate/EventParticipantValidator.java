package school.faang.user_service.Validate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipantValidator {

    private final EventParticipationRepository eventParticipationRepository;

    public void checkRegistrationAtEvent(long eventId, long userId) {
        List<User> allParticipantsByEventId = getAllParticipantsByEventId(eventId);
        if (allParticipantsByEventId.stream()
                .noneMatch(user -> user.getId() == userId)) {
            throw new IllegalArgumentException("User " + userId + " is not registered at event " + eventId);
        }
    }

    public void checkNoRegistrationAtEvent(long eventId, long userId) {
        List<User> allParticipantsByEventId = getAllParticipantsByEventId(eventId);
        if (allParticipantsByEventId.stream()
                .anyMatch(user -> user.getId() == userId)) {
            throw new IllegalArgumentException("User " + userId + "is already registered at event " + eventId);
        }
    }

    private List<User> getAllParticipantsByEventId(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }
}
