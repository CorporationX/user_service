package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EventParticipantsDto;
import school.faang.user_service.dto.EventUserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventParticipationServiceImpl implements EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final EventValidator eventValidator;
    private final UserMapper userMapper;

    public void registerParticipant(long eventId, long userId) {
        eventValidator.validateEvent(eventId);
        eventValidator.validateUser(userId);
        eventValidator.validateParticipationRegistered(eventId, userId);
        eventParticipationRepository.register(eventId, userId);
        log.info("User {} has been registered with event {}", userId, eventId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        eventValidator.validateEvent(eventId);
        eventValidator.validateUser(userId);
        eventValidator.validateParticipationNotRegistered(eventId, userId);
        eventParticipationRepository.unregister(eventId, userId);
        log.info("User {} has been unregistered with event {}", userId, eventId);
    }

    public List<EventUserDto> getParticipants(long eventId) {
        eventValidator.validateEvent(eventId);
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        return userMapper.usersToUsersDto(participants);
    }

    public EventParticipantsDto getParticipantsCount(long eventId) {
        eventValidator.validateEvent(eventId);
        int count = eventParticipationRepository.countParticipants(eventId);
        return new EventParticipantsDto(count);
    }
}
