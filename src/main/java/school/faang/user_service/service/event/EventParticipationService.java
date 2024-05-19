package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto registerParticipant(long eventId, long userId) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        for (User user : participants) {
            if (user.getId() == userId) {
                throw new RuntimeException("User is already registered for the event");
            }
        }
        eventParticipationRepository.register(eventId, userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDto(user);
    }

    public void unregisterParticipant(long eventId, long userId) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        boolean isRegistered = participants.stream().anyMatch(user -> user.getId() == userId);
        if (!isRegistered) {
            throw new RuntimeException("User is not registered for the event");
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipants(long eventId) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        return participants.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
