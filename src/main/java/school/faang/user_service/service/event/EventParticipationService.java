package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.user.UserService;

@RequiredArgsConstructor
@Service
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserService userService;
    private final EventService eventService;

    public void registerParticipant(long eventId, long userId) {
        userService.existsById(userId);
        eventService.existsById(eventId);

        validatePossibility(userId, eventId);

        eventParticipationRepository.register(eventId, userId);
    }

    private void validatePossibility(long userId, long eventId) {
        boolean exist = eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream().anyMatch(u -> u.getId() == userId);
        if (exist) {
            throw new IllegalArgumentException("User already registered");
        }
    }
}
