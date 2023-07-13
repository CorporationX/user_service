package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Service
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(long eventId, long userId) {
        User alreadyUser = findAlreadyRegisteredUser(eventId, userId);

        validateRegisterPossibility(alreadyUser);

        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        validateUnregisterPossibility(eventId, userId);

        eventParticipationRepository.unregister(eventId, userId);
    }

    private User findAlreadyRegisteredUser(long eventId, long userId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream().filter(user -> user.getId() == userId).findFirst().orElse(null);
    }

    private static void validateRegisterPossibility(User alreadyUser) {
        if (alreadyUser != null) {
            throw new IllegalArgumentException("User already registered");
        }
    }

    private void validateUnregisterPossibility(long eventId, long userId) {
        if (findAlreadyRegisteredUser(eventId, userId) == null) {
            throw new IllegalArgumentException("User not registered");
        }
    }

    public List<User> getParticipants(long eventId) {

        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
