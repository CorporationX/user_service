package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.UserNotRegisteredAtEvent;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository repository;

    public void unregisterParticipant(long eventId, long userId) {
        var user = repository.findAllParticipantsByEventId(eventId)
                .stream()
                .filter(us -> us.getId() == userId)
                .findFirst();

        if (user.isEmpty()) {
            throw new UserNotRegisteredAtEvent(eventId, userId);
        }

        repository.unregister(eventId, userId);
    }
}
