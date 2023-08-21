package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserAlreadyRegisteredAtEvent;
import school.faang.user_service.exception.UserNotRegisteredAtEvent;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
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

    public List<User> getParticipants(long eventId){
        return repository.findAllParticipantsByEventId(eventId);
    }

    public int getParticipantsCount(long eventId){
        return repository.countParticipants(eventId);
    }
}
