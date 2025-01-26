package school.faang.user_service.repository.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationAdapter {

    private final EventRepository eventRepository;

    public List<Event> findParticipatedEventsByUserId(Long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }
}