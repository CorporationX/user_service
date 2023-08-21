package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserAlreadyRegisteredAtEvent;
import school.faang.user_service.exception.UserNotRegisteredAtEvent;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository repository;
    private final UserMapper userMapper;

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
        Optional<User> user = repository.findAllParticipantsByEventId(eventId)
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

    public int getParticipantsCount(long eventId) {
        return repository.countParticipants(eventId);
    }
}
