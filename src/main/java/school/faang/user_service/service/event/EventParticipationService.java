package school.faang.user_service.service.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;

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

    private boolean isUserRegistered(Long eventId, Long userId) {
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        for (User user : users) {
            if (user.getId() == userId) {
                return false;
            }
        }
        return true;
    }
}
