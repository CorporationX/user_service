package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.event.EventParticipationValidator;

import java.util.List;
@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;
    private final EventParticipationValidator eventParticipationValidator;

    public void deleteParticipantsFromEvent(Event event) {
        List<User> attenders = event.getAttendees();
        attenders.forEach(user -> user.getParticipatedEvents().remove(event));
        }

    @Transactional
    public void registerParticipant(long eventId, long userId) {
        eventParticipationValidator.validateUserRegister(userId);
        eventParticipationRepository.register(eventId, userId);
    }

    @Transactional
    public void unregisterParticipant(long eventId, long userId) {
        eventParticipationValidator.validateUserUnregister(userId);
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipant(long eventId) {
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId
                (eventId);
        return userMapper.toDtos(users);
    }

    public int getParticipantCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
