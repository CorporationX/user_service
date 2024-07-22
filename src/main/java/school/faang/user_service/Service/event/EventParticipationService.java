package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;

    private final UserMapper userMapper;

    private boolean flag(long eventId , long userId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .anyMatch(user -> user.getId() == userId);
    }

    public void registrParticipant(long eventId, long userId) {
        if (flag(eventId , userId)){
            throw new RuntimeException("User is already registered for the event.");
        } else {
            eventParticipationRepository.register(eventId, userId);
        }
    }

    public void unregisterParticipant(long eventId, long userId) {
        if (flag(eventId , userId)){
            eventParticipationRepository.unregister(eventId, userId);
        } else {
            throw new RuntimeException("User is not registered for the event.");
        }
    }

    public List<UserDto> getPaticipant(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .map(user -> userMapper.toDto(user))
                .toList();
    }

    public int getParticipantCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
