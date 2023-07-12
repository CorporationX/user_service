package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Component
@RequiredArgsConstructor
@Service
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(long eventId, long userId) {
        User alreadyUser = eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream().filter(user -> user.getId() == userId).findFirst().orElse(null);

        validatePossibility(alreadyUser);

        eventParticipationRepository.register(eventId, userId);
    }

    private static void validatePossibility(User alreadyUser) {
        if (alreadyUser != null) {
            throw new IllegalArgumentException("User already registered");
        }
    }
}
