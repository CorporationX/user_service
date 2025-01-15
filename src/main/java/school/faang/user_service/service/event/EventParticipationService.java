package school.faang.user_service.service.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository repository;

    public void registerParticipant(long eventId, long userId) {

        if (getUserIdFromList(eventId).contains(userId))
            throw new IllegalArgumentException("User already registered for this event");
        else repository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        if (!getUserIdFromList(eventId).contains(userId))
            throw new IllegalArgumentException("User not registered for this event");
        else repository.unregister(eventId, userId);
    }

    public void getParticipant(long eventId) {
        repository.findAllParticipantsByEventId(eventId);
    }

    public void getParticipantsCount(long eventId) {
        repository.countParticipants(eventId);
    }

    private List<Long> getUserIdFromList(long eventId) {
        return repository.findAllParticipantsByEventId(eventId)
                .stream().map(User::getId)
                .collect(Collectors.toList());
    }

}
