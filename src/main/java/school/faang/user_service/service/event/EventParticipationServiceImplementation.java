package school.faang.user_service.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.RegistrationUserForEventException;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
public class EventParticipationServiceImplementation implements EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;

    @Autowired
    public EventParticipationServiceImplementation(EventParticipationRepository eventParticipationRepository) {
        this.eventParticipationRepository = eventParticipationRepository;
    }

    public void registerParticipant(Long eventId, Long userId) {
        validateInputData(eventId, userId);
        List<User> users = getParticipantsByEventId(eventId);

        if (!isUserRegisteredForEvent(users, userId)) {
            eventParticipationRepository.register(eventId, userId);
        } else {
            throw new RegistrationUserForEventException("The user has already been registered for the event");
        }
    }

    @Override
    public void unregisterParticipant(Long eventId, Long userId) {
        validateInputData(eventId, userId);
        List<User> users = getParticipantsByEventId(eventId);

        if (isUserRegisteredForEvent(users, userId)) {
            eventParticipationRepository.unregister(eventId, userId);
        } else {
            String errorMessage = String.format("the userId: [%s] is not registered for the eventId: [%s]",
                    userId, eventId);
            throw new RegistrationUserForEventException(errorMessage);
        }
    }

    @Override
    public List<User> getParticipant(Long eventId) {
        validateEventId(eventId);
        return getParticipantsByEventId(eventId);
    }

    private List<User> getParticipantsByEventId(Long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    private boolean isUserRegisteredForEvent(List<User> users, long userId) {
        return users.stream()
                .anyMatch(curUser -> curUser.getId() == userId);
    }

    private void validateInputData(Long eventId, Long userId) {
        if (eventId == null || userId == null) {
            throw new RegistrationUserForEventException("Input data is null");
        }
    }

    private void validateEventId(Long eventId) {
        if (eventId == null) {
            throw new RegistrationUserForEventException("Input data is null");
        }
    }
}
