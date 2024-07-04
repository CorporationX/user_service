package school.faang.user_service.service.eventService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.Validate.ParticipantOnEventValidator;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationServiceImpl {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;
    private final ParticipantOnEventValidator participantOnEventValidator;

    public void registerParticipant(long eventId, long userId) {
        if (!participantOnEventValidator.checkParticipantAtEvent(eventId, userId)) {
            eventParticipationRepository.register(eventId, userId);
        } else {
            throw new IllegalArgumentException("User with id " + userId + " is already registered for event with id " + eventId);
        }
    }

    public void unregisterParticipant(long eventId, long userId) {
        if (participantOnEventValidator.checkParticipantAtEvent(eventId, userId)) {
            eventParticipationRepository.unregister(eventId, userId);
        } else {
            throw new IllegalArgumentException("User with id " + userId + " is not registered for event with id " + eventId);
        }
    }

    public List<UserDto> getParticipant(long id) {
        List<User> eventUsers = eventParticipationRepository.findAllParticipantsByEventId(id);
        return eventUsers.stream()
                .map(userMapper::mapToUserDto)
                .toList();
    }

    public int getParticipantsCount(long id) {
        return eventParticipationRepository.countParticipants(id);
    }
}
