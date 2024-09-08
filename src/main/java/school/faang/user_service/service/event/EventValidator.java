package school.faang.user_service.service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EventExistsException;
import school.faang.user_service.exception.EventParticipationRegistrationException;
import school.faang.user_service.exception.UserExistsException;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Component
@Slf4j
public class EventValidator {
    private EventParticipationRepository eventParticipationRepository;
    private static final String USER_ALREADY_REGISTERED_MESSAGE = "User %d already registered on event %d";
    private static final String USER_NOT_REGISTERED_MESSAGE = "User %d does not registered on event %d";
    private static final String EVENT_NOT_EXISTS_MESSAGE = "Event %d does not exist";
    private static final String USER_NOT_EXISTS_MESSAGE = "User %d does not exist";

    public boolean validateParticipationRegistered(long eventId, long userId) {
        User participant = eventParticipationRepository.findParticipantByIdAndEventId(eventId, userId);
        if (participant != null) {
            log.error(USER_ALREADY_REGISTERED_MESSAGE.formatted(userId, eventId));
            throw new EventParticipationRegistrationException(USER_ALREADY_REGISTERED_MESSAGE.formatted(userId, eventId));
        } else {
            return false;
        }
    }

    public boolean validateParticipationNotRegistered(long eventId, long userId) {
        User participant = eventParticipationRepository.findParticipantByIdAndEventId(eventId, userId);
        if (participant == null) {
            log.error(USER_NOT_REGISTERED_MESSAGE.formatted(userId, eventId));
            throw new EventParticipationRegistrationException(USER_NOT_REGISTERED_MESSAGE.formatted(userId, eventId));
        }
        return false;
    }

    public boolean checkEventExists(long eventId) {
        if (!eventParticipationRepository.eventExistsById(eventId)) {
            log.error(EVENT_NOT_EXISTS_MESSAGE.formatted(eventId));
            throw new EventExistsException(EVENT_NOT_EXISTS_MESSAGE.formatted(eventId));
        }
        return true;
    }

    public boolean checkUserExists(long userId) {
        if (!eventParticipationRepository.userExistsById(userId)) {
            log.error(USER_NOT_EXISTS_MESSAGE.formatted(userId));
            throw new UserExistsException(USER_NOT_EXISTS_MESSAGE.formatted(userId));
        }
        return true;
    }
}
