package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(long eventId, long userId) {
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        for (User user : users) {
            if (user.getId() == userId) {
                throw new DataValidationException("You are registered already!");
            }
        }
        eventParticipationRepository.register(eventId, userId);

    }

    public void unregisterParticipant(long eventId, long userId) {
        if (checkAnyUserInEvent(eventId, userId)) {
            eventParticipationRepository.unregister(eventId, userId);
        } else {
            throw new DataValidationException("You are not registered");
        }
    }

    public boolean checkAnyUserInEvent(long eventId, long userId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .anyMatch(user -> user.getId() == userId);
    }
}