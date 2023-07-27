package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.UserAlreadyRegisteredException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    public void registerParticipant(long eventId, long userId) {
        if (isParticipantRegistered(eventId, userId)) {
            throw new UserAlreadyRegisteredException(
                String.format("User with ID=%d is already registered for the event with ID=%d", userId, eventId)
            );
        }
        eventParticipationRepository.register(eventId, userId);
    }

    public int getParticipantsCount(Long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }

    public List<UserDto> getAllParticipants(Long eventId) {
        return userMapper.toDtoList(eventParticipationRepository.findAllParticipantsByEventId(eventId));
    }

    private boolean isParticipantRegistered(long eventId, long userId) {
        return getAllParticipants(eventId).stream().anyMatch(p -> p.getId() == userId);
    }
}
