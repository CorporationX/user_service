package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final EventService eventService;
    private final UserService userService;
    private final UserMapper userMapper;

    public int findParticipantsAmountByEventId(long eventId) {
        eventExistsOrThrow(eventId);
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).size();
    }

    public List<UserDto> findAllParticipantsByEventId(long eventId) {
        eventExistsOrThrow(eventId);
        return eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public void registerParticipant(long eventId, long userId) {
        validateInput(eventId, userId);
        eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream()
                .filter(user -> user.getId() == userId)
                .findFirst()
                .ifPresentOrElse(
                        user -> {
                            throw new IllegalStateException("User with id " + userId + " is already registered for the event");
                        },
                        () -> eventParticipationRepository.register(eventId, userId));
    }

    public void unregisterParticipant(long eventId, long userId) {
        validateInput(eventId, userId);
        eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream()
                .filter(user -> user.getId() == userId)
                .findFirst()
                .ifPresentOrElse(
                        user -> eventParticipationRepository.unregister(eventId, userId),
                        () -> {
                            throw new IllegalStateException("User with id " + userId + " is not registered for the event");
                        });
    }

    private void validateInput(long eventId, long userId) {
        eventExistsOrThrow(eventId);
        userExistsOrThrow(userId);
    }

    private void userExistsOrThrow(long userId) {
        if (!userService.checkUserExistence(userId)) {
            throw new EntityNotFoundException("User with id " + userId + " does not exist");
        }
    }

    private void eventExistsOrThrow(long eventId) {
        if (!eventService.checkEventExistence(eventId)) {
            throw new EntityNotFoundException("Event with id " + eventId + " does not exist");
        }
    }
}
