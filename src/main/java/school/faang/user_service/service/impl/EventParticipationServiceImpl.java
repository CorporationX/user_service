package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.repository.EventParticipationRepository;
import school.faang.user_service.service.EventParticipationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationServiceImpl implements EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;

    @Override
    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }

    @Override
    public List<User> getParticipant(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    @Override
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