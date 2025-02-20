package school.faang.user_service.repository.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Component
@RequiredArgsConstructor
public class EventParticipationRepositoryAdapter {

    private final EventParticipationRepository eventParticipationRepository;

    public void unregisterAll(long eventId) {
        eventParticipationRepository.unregisterAll(eventId);
    }
}
