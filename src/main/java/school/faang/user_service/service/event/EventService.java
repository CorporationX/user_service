package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    Event create(Event event);

    Event updateEvent(Event event);

    Event getEvent(Long eventId);

    List<Event> getEventsByFilter(EventFilters eventFilters);

    List<Event> getOwnedEvents(Long userId);

    List<Event> getParticipatedEvents(Long userId);

    Integer getSubscribersCount(Event event);

    void deleteEvent(Long eventId);

    void startEventsFromPeriod(LocalDateTime from, LocalDateTime to);
}
