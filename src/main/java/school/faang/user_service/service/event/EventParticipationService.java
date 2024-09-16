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
    public void registerParticipant(Long eventId, Long userId) {
        validateEventAndUserIds(eventId, userId);
        eventParticipationRepository.register(eventId, userId);
    }

    @Transactional
    public void unregisterParticipant(Long eventId, Long userId) {
        validateEventAndUserIds(eventId, userId);
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<User> getParticipant(Long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    public long getParticipantsCount(Long eventId) {
        return eventParticipationRepository
                .findAllParticipantsByEventId(eventId)
                .stream()
                .count();
    }

    public void validateEventAndUserIds(Long eventId, Long userId){
        if (!eventParticipationRepository.existsById(eventId)) {
            throw new IllegalArgumentException("Event does not exist");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User does not exist"));
        List<User> allParticipants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        if (allParticipants.contains(user)) {
            throw new IllegalArgumentException("User already registered");
        }
    }
}
