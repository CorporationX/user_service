package school.faang.user_service.service.eventServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.EventParticipationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationServiceImpl implements EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    @Override
    public void registerParticipant(long eventId, long userId) {
        if (eventParticipationRepository.checkParticipantAtEvent(eventId, userId) == 0) {
            eventParticipationRepository.register(eventId, userId);
        } else {
            throw new IllegalArgumentException("User with id " + userId + " is already registered for event with id " + eventId);
        }
    }

    @Override
    public void unregisterParticipant(long eventId, long userId) {
        if (eventParticipationRepository.checkParticipantAtEvent(eventId, userId) > 0) {
            eventParticipationRepository.unregister(eventId, userId);
        } else {
            throw new IllegalArgumentException("User with id " + userId + " is not registered for event with id " + eventId);
        }
    }

    @Override
    public List<UserDto> getParticipant(long id) {
        List<User> eventUsers = eventParticipationRepository.findAllParticipantsByEventId(id);
        return eventUsers.stream()
                .map(userMapper::mapToUserDto)
                .toList();
    }

    @Override
    public int getParticipantsCount(long id) {
        return eventParticipationRepository.countParticipants(id);
    }
}
