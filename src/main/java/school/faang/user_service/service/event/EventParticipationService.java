package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public void registerParticipant(long eventId, long userId) {
        validator(eventId, userId);
        userValidator.validateUserRegister(userId);
        eventParticipationRepository.register(eventId, userId);
    }

    @Transactional
    public void unregisterParticipant(long eventId, long userId) {
        validator(eventId, userId);
        userValidator.validateUserUnregister(userId);
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipant(Long eventId) {
        eventValidator.validateEventId(eventId);
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId
                (eventId);
        return userMapper.toDtos(users);
    }

    public int getParticipantCount(long eventId) {
        eventValidator.validateEventId(eventId);
        return eventParticipationRepository.countParticipants(eventId);
    }

    private void validator(long eventId, long userId) {
        userValidator.validateUserId(userId);
        eventValidator.validateEventId(eventId);
    }
}
