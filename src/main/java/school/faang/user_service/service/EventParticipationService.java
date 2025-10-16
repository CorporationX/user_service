package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor

public class EventParticipationService {
    private final EventParticipationRepository repository;

    public void registerParticipant(long eventId, long userId) {

        if (isUserRegisteredOnEvent(eventId))
            throw new IllegalArgumentException("User already registered for this event");
        else repository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        if (!isUserRegisteredOnEvent(eventId))
            throw new IllegalArgumentException("User not registered for this event");
        else repository.unregister(eventId, userId);
    }

    public List<User> getParticipant(long eventId) {
        return repository.findAllParticipantsByEventId(eventId);
    }

    public int getParticipantsCount(long eventId) {
        return repository.countParticipants(eventId);
    }

    private boolean isUserRegisteredOnEvent(long eventId) {
        List<Long> getListOfUserId = repository.findAllParticipantsByEventId(eventId).stream()
                .map(User::getId).toList();
        return getListOfUserId.contains(eventId);

    }

}
