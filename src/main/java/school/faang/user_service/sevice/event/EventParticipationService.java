package school.faang.user_service.sevice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository repository;
    private final UserMapper mapper;

    public void registerParticipant(long eventId, long userId) {
        if (repository.findAllParticipantsByEventId(eventId).stream()
                .map(User::getId)
                .anyMatch(id -> id == userId)) {
            throw new IllegalArgumentException("cant register user: user already" + userId + " register to event " + eventId);
        }

        repository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        if (repository.findAllParticipantsByEventId(eventId).stream()
                .map(User::getId)
                .anyMatch(id -> id == userId)) {
            repository.unregister(eventId, userId);
        } else {
            throw new IllegalArgumentException("cant unregister user: user do not" + userId + " register to event " + eventId);
        }
    }

    public List<UserDto> getParticipant(long eventId) {
        return mapper.toDtoList(repository.findAllParticipantsByEventId(eventId));
    }

    public Integer getParticipantsCount(long eventId) {
        return repository.countParticipants(eventId);
    }
}
