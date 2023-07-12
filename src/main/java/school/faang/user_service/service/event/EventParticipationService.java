package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Component
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(long eventId, long userId) {
        try {
            User alreadyUser = eventParticipationRepository.findAllParticipantsByEventId(eventId)
                    .stream().filter(user -> user.getId() == userId).findFirst().orElse(null);

            if (alreadyUser != null) {
                throw new IllegalArgumentException("User already registered");
            }

            eventParticipationRepository.register(eventId, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
