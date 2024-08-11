package school.faang.user_service.validator.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.user.UserValidator;

@Component
@RequiredArgsConstructor
public class EventParticipationValidator {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserValidator userValidator;
    private final EventServiceValidator eventServiceValidator;

    private boolean hasAnyParticipant(long eventId, long userId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .anyMatch(user -> user.getId() == userId);
    }

    public void validateCanUserRegister(long eventId, long userId) {
        validateAreEventExist(eventId);
        validateAreUserExist(userId);
        if (hasAnyParticipant(eventId, userId)) {
            throw new IllegalArgumentException("User " + userId + " is already registered on event " + eventId);
        }
    }

    public void validateCanUserUnregister(long eventId, long userId) {
        validateAreEventExist(eventId);
        validateAreUserExist(userId);
        if (!hasAnyParticipant(eventId, userId)) {
            throw new IllegalArgumentException("User " + userId + " is not registered on event " + eventId);
        }
    }

    public void validateAreUserExist(long userId) {
        userValidator.validateThatUserIdExist(userId);
    }

    public void validateAreEventExist(long eventId) {
        eventServiceValidator.validateEventId(eventId);
    }
}
