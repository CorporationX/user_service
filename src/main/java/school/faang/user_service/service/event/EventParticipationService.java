package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.commonMessages.ErrorMessagesForEvent;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.RegistrationUserForEventException;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(Long eventId, Long userId) {
        validateInputData(eventId, userId);
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        if (isUserRegisteredForEvent(users, userId)) {
            Object[] args = {userId, eventId};
            String errMessage = ErrorMessagesForEvent.USER_IS_ALREADY_REGISTERED.format(args);
            throw new RegistrationUserForEventException(errMessage);
        }

        eventParticipationRepository.register(eventId, userId);
    }

    private boolean isUserRegisteredForEvent(List<User> users, long userId) {
        return users.stream()
                .anyMatch(curUser -> curUser.getId() == userId);
    }

    private void validateInputData(Long eventId, Long userId) {
        if (eventId == null || userId == null) {
            String errMessage = ErrorMessagesForEvent.INPUT_DATA_IS_NULL;
            throw new RegistrationUserForEventException(errMessage);
        }
    }
}
