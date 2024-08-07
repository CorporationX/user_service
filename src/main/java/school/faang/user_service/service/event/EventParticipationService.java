package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.EventParticipationValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final EventParticipationValidator eventParticipationValidator;
    private final UserMapper userMapper;

    @Transactional
    public void registerParticipant(long userId, long eventId) {
        eventParticipationValidator.checkUserIsExisting(userId);
        eventParticipationValidator.checkEventIsExisting(eventId);
        eventParticipationValidator.checkIsUserAlreadyRegistered(userId, eventId);
        eventParticipationRepository.register(eventId, userId);
    }

    @Transactional
    public void unregisterParticipant(long userId, long eventId) {
        eventParticipationValidator.checkUserIsExisting(userId);
        eventParticipationValidator.checkEventIsExisting(eventId);
        eventParticipationValidator.checkIsUserNotRegistered(userId, eventId);
        eventParticipationRepository.unregister(eventId, userId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getParticipant(long eventId) {
        eventParticipationValidator.checkEventIsExisting(eventId);
        return eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream().map(userMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public int getParticipantsCount(long eventId) {
        eventParticipationValidator.checkEventIsExisting(eventId);
        return eventParticipationRepository.countParticipants(eventId);
    }
}
