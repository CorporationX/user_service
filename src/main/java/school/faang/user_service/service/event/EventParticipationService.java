package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.util.UserUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(long eventId, long userId) {
        Optional<User> userById = eventParticipationRepository.findById(userId);

        if (userById.isEmpty()) {
            eventParticipationRepository.register(eventId, userId);
        } else {
            throw new IllegalArgumentException("User is already registered");
        }
    }

    public void unregisterParticipant(long eventId, long userId) {
        List<User> listUsersAtEvent = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        if (UserUtils.findUserById(listUsersAtEvent, userId)) {
            eventParticipationRepository.unregister(eventId, userId);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public List<User> getParticipant(long eventId) {
        if (!eventParticipationRepository.existsById(eventId)) {
            throw new IllegalArgumentException("Event not found with id: " + eventId);
        }
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    public int getParticipantsCount(long eventId) {
        if (!eventParticipationRepository.existsById(eventId)) {
            throw new IllegalArgumentException("Event not found with id: " + eventId);
        }
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).size();
    }
}
