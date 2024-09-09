package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@RequiredArgsConstructor
public class EventServiceImpl implements EventService{
    private final EventRepository eventRepository;


    @Override
    public void removeEvents(List<Event> events) {
        eventRepository.deleteAll(events);
    }
}
