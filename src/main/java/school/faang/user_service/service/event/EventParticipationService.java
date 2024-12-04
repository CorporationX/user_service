package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository repository;
    private final UserMapper userMapper;

    @Transactional
    public void registerParticipant(long eventId, long userId) {
        if (checkRegistration(eventId, userId)) {
            throw new IllegalStateException("User is already registered to event");
        }
        repository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        if (!checkRegistration(eventId, userId)) {
            throw new IllegalStateException("User is not registered to event");
        }
        repository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipant(long eventId) {
        checkEventById(eventId);
        return repository.findAllParticipantsByEventId(eventId).stream()
                .map(userMapper::toDto)
                .toList();
    }

    public Integer getParticipantsCount(long eventId) {
        checkEventById(eventId);
        return repository.countParticipants(eventId);
    }

    private void checkEventById(long eventId) {
        if (repository.findAllParticipantsByEventId(eventId).isEmpty()) {
            throw new IllegalStateException("There is no event with this id");
        }
    }

    private boolean checkRegistration(long eventId, long userId) {
        return repository.findAllParticipantsByEventId(eventId).stream()
                .anyMatch(user -> user.getId().equals(userId));
    }

}
