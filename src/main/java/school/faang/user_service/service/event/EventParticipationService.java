package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }

    public List<User> getParticipant(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    public void manageParticipation(long eventId, long userId, boolean isRegistering) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        boolean isRegistered = participants.stream()
                .anyMatch(user -> user.getId() == userId);

        if (isRegistered) {
                if (isRegistering) {
                throw new IllegalArgumentException("User already registered");
                }
                eventParticipationRepository.register(eventId, userId);
        } else {
            if (!isRegistering) {
                throw new IllegalArgumentException("User is not registered");
            }
            eventParticipationRepository.unregister(eventId, userId);
        }
    }
}