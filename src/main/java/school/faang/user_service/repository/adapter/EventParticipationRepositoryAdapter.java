package school.faang.user_service.repository.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationRepositoryAdapter {

    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;

    public List<Event> findParticipatedEventsByUserId(Long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }

    public void unregisterAll(long eventId) {
        eventParticipationRepository.unregisterAll(eventId);
    }
}
