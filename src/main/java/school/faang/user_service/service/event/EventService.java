package school.faang.user_service.service.event;

import org.springframework.scheduling.annotation.Async;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EventService {

    EventDto create(EventDto eventDto);

    EventDto getEvent(long eventId);

    List<EventDto> getEventsByFilter(EventFilterDto filters);

    void deleteEvent(long eventId);

    void updateEvent(EventDto eventDto);

    List<EventDto> getOwnedEvents(long userId);

    List<EventDto> getParticipatedEvents(long userId);

    CompletableFuture<Void> clearOutdatedEvents(List<Event> events);
}
