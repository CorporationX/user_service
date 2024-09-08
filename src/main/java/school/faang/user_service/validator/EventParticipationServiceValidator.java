package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Component
@RequiredArgsConstructor
public class EventParticipationServiceValidator {
    private final EventParticipationRepository repository;

    public void validateUserRegister(long eventId, long userId) throws IllegalArgumentException {
        if (repository.findAllParticipantsByEventId(eventId).stream()
                .map(User::getId)
                .anyMatch(id -> id == userId)) {
            throw new IllegalArgumentException("cant register user: user already" + userId + " register to event " + eventId);
        }
    }

    public void validateUserUnregister(long eventId, long userId) throws IllegalArgumentException {
        if (repository.findAllParticipantsByEventId(eventId).stream()
                .map(User::getId)
                .noneMatch(id -> id == userId)) {
            throw new IllegalArgumentException("cant unregister user: user do not" + userId + " register to event " + eventId);
        }
    }

    public void validateEvent(long eventId) {
        if (!repository.existsById(eventId)) {
            throw new IllegalArgumentException("do not have event with id = "
                    + eventId + " in repo");
        }
    }
}
