package school.faang.user_service.service.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final EventRepository eventRepository;
    private final UserMapper userMapper;

    public void registerParticipant(Long eventId, Long userId) {
        if (!isUserRegistered(eventId, userId)) {
            throw new DataValidationException("User already registered");
        }
        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(Long eventId, Long userId) {
        if (isUserRegistered(eventId, userId)) {
            throw new DataValidationException("User not registered");
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipants(Long eventId) {
        validateEventId(eventId);
        List<User> usersList = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        return usersList.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public int getParticipantsCount(Long eventId) {
        validateEventId(eventId);
        return eventParticipationRepository.countParticipants(eventId);
    }

    private boolean isUserRegistered(Long eventId, Long userId) {
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        for (User user : users) {
            if (user.getId() == userId) {
                return false;
            }
        }
        return true;
    }

    private void validateEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new DataValidationException("Event not found");
        }
    }
}
