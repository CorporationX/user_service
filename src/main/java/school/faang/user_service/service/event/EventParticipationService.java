package school.faang.user_service.service.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void registerParticipant(long eventId, long userId) {
        if (!eventParticipationRepository.existsById(eventId)) {
            throw new RuntimeException("Event does not exist");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User does not exist"));
        List<User> allParticipants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        if (allParticipants.contains(user)) {
            throw new RuntimeException("User already registered");
        }
        eventParticipationRepository.register(eventId, userId);
    }

    @Transactional
    public void unregisterParticipant(long eventId, long userId) {
        if (!eventParticipationRepository.existsById(eventId)) {
            throw new RuntimeException("Event does not exist");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User does not exist"));
        List<User> allParticipants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        if (!allParticipants.contains(user)) {
            throw new RuntimeException("User was not registered");
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<User> getParticipant(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    public long getParticipantsCount(long eventId) {
        return eventParticipationRepository
                .findAllParticipantsByEventId(eventId)
                .stream()
                .count();
    }
}
