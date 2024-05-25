package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    @Transactional
    public void registerParticipant(long eventId, long userId) throws IllegalArgumentException {
        if (eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream().anyMatch(user -> user.getId() == userId)) {
            throw new IllegalArgumentException("User is already registered for this event.");
        }

        eventParticipationRepository.register(eventId, userId);
    }

    @Transactional
    public void unregisterParticipant(long eventId, long userId) throws IllegalArgumentException {
        if (eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream().noneMatch(user -> user.getId() == userId)) {
            throw new IllegalArgumentException("User is not registered for the event.");
        }

        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipants(long eventId) {
        return userMapper.toDtoList(eventParticipationRepository.findAllParticipantsByEventId(eventId));
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}