package school.faang.user_service.service.event;

import school.faang.user_service.entity.event.Event;

import java.util.List;

public interface EventService {
    void removeEvents(List<Event> events);
}
