package school.faang.user_service.validator.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.EventParticipationRegistrationException;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Component
@AllArgsConstructor
public class EventParticipationValidator {
    private static final String USER_ALREADY_REGISTERED_MESSAGE = "User %d already registered on event %d";
    private static final String USER_NOT_REGISTERED_MESSAGE = "User %d does not registered on event %d";
    private static final String EVENT_NOT_EXISTS_MESSAGE = "Event %d does not exist";
    private static final String USER_NOT_EXISTS_MESSAGE = "User %d does not exist";

    private final EventParticipationRepository eventParticipationRepository;

    public void validateParticipationRegistered(long eventId, long userId) {
        User participant = eventParticipationRepository.findParticipantByIdAndEventId(eventId, userId);
        if (participant != null) {
            throw new EventParticipationRegistrationException(
                    USER_ALREADY_REGISTERED_MESSAGE.formatted(userId, eventId));
        }
    }

    public void validateParticipationNotRegistered(long eventId, long userId) {
        User participant = eventParticipationRepository.findParticipantByIdAndEventId(eventId, userId);
        if (participant == null) {
            throw new EventParticipationRegistrationException(USER_NOT_REGISTERED_MESSAGE.formatted(userId, eventId));
        }
    }

    public void checkEventExists(long eventId) {
        if (!eventParticipationRepository.eventExistsById(eventId)) {
            throw new EntityNotFoundException(EVENT_NOT_EXISTS_MESSAGE.formatted(eventId));
        }
    }

    public void checkUserExists(long userId) {
        if (!eventParticipationRepository.userExistsById(userId)) {
            throw new EntityNotFoundException(USER_NOT_EXISTS_MESSAGE.formatted(userId));
        }
    }
}