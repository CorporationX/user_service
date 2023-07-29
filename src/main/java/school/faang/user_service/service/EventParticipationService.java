package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exeptions.NoOneParticipatesInTheEvent;
import school.faang.user_service.exeptions.UserAlreadyRegisteredAtEvent;
import school.faang.user_service.exeptions.UserAreNotRegisteredAtEvent;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;


@Component
@AllArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(long eventId, long userId) {
        var user = eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream()
                .filter(person -> person.getId() == userId)
                .findFirst();
        if (user.isPresent()) {
            throw new UserAlreadyRegisteredAtEvent(eventId, userId);
        }
        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        var user = eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream()
                .filter(person -> person.getId() == userId)
                .findFirst();
        if (user.isPresent()) {
            eventParticipationRepository.unregister(eventId, userId);
        } else {
            throw new UserAreNotRegisteredAtEvent(eventId, userId);
        }
    }

    public List<User> getParticipant(long eventId) {
        var users = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        if (users.size() == 0) {
            throw new NoOneParticipatesInTheEvent(eventId);
        } else {
            return users;
        }
    }

}
