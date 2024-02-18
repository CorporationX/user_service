package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.mapper.user.UserMapper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final EventRepository eventRepository;
    private final UserMapper userMapper;

    private boolean findUserById(List<UserDto> userList, long userId) {
        for (UserDto user : userList) {
            if (user.getId() == userId) {
                return true;
            }
        }
        return false;
    }

    private List<UserDto> getUsersAndConvertToUserDtoList(long eventId) {
        return userMapper.toUserDtoList(eventParticipationRepository.findAllParticipantsByEventId(eventId));
    }

    @Transactional
    public void registerParticipant(long eventId, long userId) {
        Optional<UserDto> userById = eventParticipationRepository.findById(userId).map(userMapper::toDto);
        List<UserDto> listUsersAtEvent = getUsersAndConvertToUserDtoList(eventId);
        if (!eventRepository.existsById(eventId)) {
            throw new IllegalArgumentException("Event not found with id: " + eventId);
        }
        if (userById.isPresent() && !findUserById(listUsersAtEvent, userId)) {
            eventParticipationRepository.register(eventId, userId);
        } else {
            throw new IllegalArgumentException("User is already registered with id:" + userId);
        }
    }

    @Transactional
    public void unregisterParticipant(long eventId, long userId) {
        List<UserDto> listUsersAtEvent = getUsersAndConvertToUserDtoList(eventId);
        if (findUserById(listUsersAtEvent, userId)) {
            eventParticipationRepository.unregister(eventId, userId);
        } else {
            throw new IllegalArgumentException("User not found with id:" + userId);
        }
    }

    public List<UserDto> getParticipant(long eventId) {
        if (!eventParticipationRepository.existsById(eventId)) {
            throw new IllegalArgumentException("Event not found with id: " + eventId);
        }
        return getUsersAndConvertToUserDtoList(eventId);
    }

    public int getParticipantsCount(long eventId) {
        if (!eventParticipationRepository.existsById(eventId)) {
            throw new IllegalArgumentException("Event not found with id: " + eventId);
        }
        return eventParticipationRepository.countParticipants(eventId);
    }
}
