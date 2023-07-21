package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserService userService;
    private final EventService eventService;
    private final UserMapper userMapper;

    public void registerParticipant(long eventId, long userId) {
        userService.existsById(userId);
        eventService.existsById(eventId);

        if (isExist(userId, eventId)) {
            throw new DataValidationException("User already registered");
        }

        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        userService.existsById(userId);
        eventService.existsById(eventId);

        if (!isExist(userId, eventId)) {
            throw new DataValidationException("User was not registered");
        }

        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipants(long eventId) {
        eventService.existsById(eventId);
        return eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public int getParticipantsCount(long eventId) {
        eventService.existsById(eventId);
        return eventParticipationRepository.countParticipants(eventId);
    }

    private boolean isExist(long userId, long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream().anyMatch(u -> u.getId() == userId);
    }
}
