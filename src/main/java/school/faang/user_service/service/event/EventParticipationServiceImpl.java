package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EventNotFoundException;
import school.faang.user_service.exception.UserAlreadyRegisteredException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.exception.UserNotRegisteredForEventException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationServiceImpl implements EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public void registerParticipant(long eventId, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "User with ID " + userId + " not found");
        }

        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException(
                    "Event with ID " + eventId + " not found");
        }

        if (isUserRegisteredForEvent(eventId, userId)) {
            throw new UserAlreadyRegisteredException(
                    "User with ID " + userId + " is already registered for event with ID " + eventId);
        }
        eventParticipationRepository.register(eventId, userId);
    }

    @Override
    public void unregisterParticipant(long eventId, long userId) {
        if (!isUserRegisteredForEvent(eventId, userId)) {
            throw new UserNotRegisteredForEventException(
                    "User with ID " + userId + " is not registered for event with ID " + eventId);
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    @Override
    public List<UserDto> getParticipants(long eventId) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        return userMapper.toDtoList(participants);
    }

    @Override
    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }

    private boolean isUserRegisteredForEvent(long eventId, long userId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .anyMatch(user -> user.getId() == userId);
    }
}
