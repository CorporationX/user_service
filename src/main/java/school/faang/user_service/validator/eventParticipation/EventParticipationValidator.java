package school.faang.user_service.validator.eventParticipation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationValidator {
    private final EventParticipationRepository eventParticipationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public void checkUserIsExisting(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException("User doesn't exist in the system ID = " + userId);
        }
    }

    public void checkEventIsExisting(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new DataValidationException("Event doesn't exist in the system ID = " + eventId);
        }
    }

    public void checkIsUserAlreadyRegistered(long userId, long eventId) {
        List<User> checkedUsers = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        List<Long> checkedUserIds = checkedUsers.stream().map(User::getId).toList();

        if (checkedUserIds.contains(userId)) {
            throw new DataValidationException("User with ID: " + userId + "has already registered");
        }
    }

    public void checkIsUserNotRegistered(long userId, long eventId) {
        List<User> checkedUsers = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        List<Long> checkedUserIds = checkedUsers.stream().map(User::getId).toList();

        if (!checkedUserIds.contains(userId)) {
            throw new DataValidationException("User with ID: " + userId + "has not registered");
        }
    }
}
