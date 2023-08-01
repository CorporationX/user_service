package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exeptions.*;
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
            throw new UserAlreadyRegisteredAtEventExeption(eventId, userId);
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
            throw new UserAreNotRegisteredAtEventExeption(eventId, userId);
        }
    }

    public List<User> getParticipant(long eventId) {
        var users = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        if (users.isEmpty()) {
            throw new NoOneParticipatesInTheEventExeption(eventId);
        } else {
            return users;
        }
    }

    public int getParticipantsCount(long eventID){
        return eventParticipationRepository.countParticipants(eventID);
    }

}
