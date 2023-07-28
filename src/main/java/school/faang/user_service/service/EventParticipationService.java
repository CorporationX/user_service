package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.UserAlreadyRegisteredAtEvent;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Component
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository repository;

    public void registerParticipant(long userId, long eventId) {
        var user = repository.findAllParticipantsByEventId(eventId)
                .stream()
                .filter(us -> us.getId() == userId)
                .findFirst();

        if (user.isPresent()) {
            throw new UserAlreadyRegisteredAtEvent(userId, eventId);
        }

        repository.register(eventId, userId);
    }
}
