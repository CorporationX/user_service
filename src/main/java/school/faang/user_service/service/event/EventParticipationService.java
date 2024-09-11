package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.event.EventValidator;
import school.faang.user_service.validator.user.UserValidator;

import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final EventValidator eventValidator;

    public void registerParticipant(long eventId, long userId) {
        userValidator.userIdIsNotNullOrElseThrowValidationException(userId);
        eventValidator.eventIdIsNotNullOrElseThrowValidationException(eventId);
        eventValidator.checkIfRegisterParticipantThenThrowException(userId);
        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        userValidator.userIdIsNotNullOrElseThrowValidationException(userId);
        eventValidator.eventIdIsNotNullOrElseThrowValidationException(eventId);
        eventValidator.checkIfUnregisterParticipantThenThrowException(userId);
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipant(Long eventId) {
        eventValidator.checkIfUnregisterParticipantThenThrowException(eventId);
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId
                (eventId);
        return userMapper.toDtos(users);
    }

    public int getParticipantCount(long eventId) {
        eventValidator.eventIdIsNotNullOrElseThrowValidationException(eventId);
        return eventParticipationRepository.countParticipants(eventId);
    }
}
