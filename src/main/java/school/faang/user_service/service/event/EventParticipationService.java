package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.event.EventValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final EventValidator eventValidator;

    public void registerParticipant(long eventId, long userId) {
        userValidator.checkUserIdIsNotNull(userId);
        eventValidator.eventIdIsNotNullOrElseThrowValidationException(eventId);
        eventValidator.checkIfUserRegisterOnEvent(userId);
        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        userValidator.checkUserIdIsNotNull(userId);
        eventValidator.eventIdIsNotNullOrElseThrowValidationException(eventId);
        eventValidator.checkIfUserUnregisterOnEvent(userId);
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipant(Long eventId) {
        eventValidator.eventIdIsNotNullOrElseThrowValidationException(eventId);
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId
                (eventId);
        return userMapper.toDtos(users);
    }

    public int getParticipantCount(long eventId) {
        eventValidator.eventIdIsNotNullOrElseThrowValidationException(eventId);
        return eventParticipationRepository.countParticipants(eventId);
    }
}
