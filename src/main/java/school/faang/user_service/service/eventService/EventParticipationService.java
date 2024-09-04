package school.faang.user_service.service.eventService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.EventParticipantValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;
    private final EventParticipantValidator eventParticipantValidator;

    public void registerParticipant(long eventId, long userId) {
        eventParticipantValidator.checkNoRegistrationAtEvent(eventId, userId);
        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        eventParticipantValidator.checkRegistrationAtEvent(eventId, userId);
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipant(long id) {
        List<User> eventUsers = eventParticipationRepository.findAllParticipantsByEventId(id);
        return eventUsers.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public int getParticipantsCount(long id) {
        return eventParticipationRepository.countParticipants(id);
    }
}
